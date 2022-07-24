package com.numq.stash.loading

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import com.numq.stash.files.ImageFile
import com.numq.stash.notification.NotificationApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LoadingService constructor(
    private val context: Context,
    private val contentResolver: ContentResolver,
    private val notification: NotificationApi
) : LoadingApi {

    private val imageType = "image/*"
    private val zipType = "application/zip"

    private val downloads =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    private fun showNotification(file: File, type: String) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        notification.showDownloadNotification(uri.toString(), type)
    }

    private fun generateName(extension: String, index: String = "") =
        "${System.currentTimeMillis()}$index.${extension}"

    private fun download(file: File, type: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, type)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                })
                ?.let { contentResolver.openOutputStream(it) }
                ?.use {
                    it.write(file.readBytes())
                    true
                } != null
        } else {
            file.createNewFile()
        }
    }

    override fun upload(uri: String, onUpload: (ImageFile) -> Boolean) = runCatching {
        Uri.parse(uri)?.let {
            contentResolver.getType(it)
                ?.split("/")
                ?.takeLast(1)
                ?.toTypedArray()
                ?.let { (extension) ->
                    contentResolver.openInputStream(it).use { stream ->
                        val bitmapExtension =
                            if (extension == "png") Pair(extension, Bitmap.CompressFormat.PNG)
                            else Pair("jpeg", Bitmap.CompressFormat.JPEG)
                        ByteArrayOutputStream().use { output ->
                            BitmapFactory.decodeStream(stream)
                                .compress(
                                    bitmapExtension.second,
                                    100,
                                    output
                                )
                            val blob = Base64.encodeToString(
                                output.toByteArray(),
                                Base64.DEFAULT
                            )
                            return onUpload(ImageFile(extension, blob))
                        }
                    }
                }
        }
    }.isSuccess

    override fun downloadOne(file: ImageFile) = runCatching {
        val target = File(downloads, generateName(file.extension))
        FileOutputStream(target).use {
            it.write(file.blob.toByteArray())
            true
        }.also { download(target, imageType);showNotification(target, imageType) }
    }.isSuccess

    override fun downloadMultiple(files: List<ImageFile>) = files.map(::downloadOne).all { true }

    override fun downloadZip(files: List<ImageFile>) = runCatching {
        val target = File(downloads, generateName("zip"))
        ZipOutputStream(FileOutputStream(target)).use { zip ->
            files.mapIndexed { index, file ->
                val name = generateName(file.extension, index.toString())
                zip.putNextEntry(ZipEntry(name))
                zip.write(file.blob.toByteArray())
                true
            }.all { true }.also { showNotification(target, zipType) }
        }
        return download(target, zipType)
    }.isSuccess
}