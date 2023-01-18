package com.numq.stash.file

sealed class FileEvent private constructor() {
    object Empty : FileEvent()
    object Refresh : FileEvent()
    data class Upload(val file: File) : FileEvent()
    data class Delete(val file: File) : FileEvent()
}