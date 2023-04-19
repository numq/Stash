package com.numq.stash.transfer

import com.numq.stash.file.File

sealed class TransferAction private constructor() {
    object Upload : TransferAction()
    data class DownloadFile(val file: File) : TransferAction()
    data class DownloadMultipleFiles(val files: List<File>) : TransferAction()
    data class DownloadZip(val files: List<File>) : TransferAction()
}