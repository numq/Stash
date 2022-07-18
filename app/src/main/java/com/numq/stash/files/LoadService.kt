package com.numq.stash.files

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LoadService constructor(
    private val contentResolver: ContentResolver
) : LoadApi {

    private fun download(file: ImageFile, index: String = ""): Boolean {
        try {
            val name = "${System.currentTimeMillis()}$index.jpg"
            val compressBitmap: (OutputStream?) -> Boolean = {
                BitmapFactory.decodeStream(file.blob.inputStream())
                    .compress(Bitmap.CompressFormat.JPEG, 100, it)
                true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    })?.let { contentResolver.openOutputStream(it) }.use(compressBitmap)
            } else {
                val downloads =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val target = File(downloads, name)
                if (!target.exists()) return FileOutputStream(target).use(compressBitmap)
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.e(javaClass.simpleName, it) }
        }
        return false
    }

    override fun upload(uri: String, onUpload: (ImageFile) -> Boolean): Boolean {
        Uri.parse(uri)?.let { parsedUri ->
            val extension = contentResolver.getType(parsedUri)
            extension?.let {
                val stream = ByteArrayOutputStream().use { output ->
                    BitmapFactory.decodeStream(contentResolver.openInputStream(parsedUri))
                        .compress(Bitmap.CompressFormat.JPEG, 100, output)
                    output
                }
                val blob = "data:${extension};base64,${
                    Base64.encodeToString(
                        stream.toByteArray(),
                        Base64.DEFAULT
                    )
                }"
                return onUpload(ImageFile(blob.toByteArray()))
            }
        }
        return false
    }

    override fun downloadOne(file: ImageFile) = download(file)

    override fun downloadMultiple(files: List<ImageFile>) =
        files.mapIndexed { idx, file -> download(file, idx.toString()) }.all { true }

    override fun downloadZip(files: List<ImageFile>): Boolean {
        val downloads =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val target = File(downloads, "${System.currentTimeMillis()}.zip")
        if (!target.exists()) {
            return ZipOutputStream(FileOutputStream(target)).use { zip ->
                files.mapIndexed { index, file ->
                    file.blob.inputStream().use {
                        val name = "${System.currentTimeMillis()}$index.jpg"
                        val entry = ZipEntry(name)
                        zip.putNextEntry(entry)
                        zip.write(file.blob)
                        true
                    }
                }.all { true }
            }
        }
        return false
    }
}