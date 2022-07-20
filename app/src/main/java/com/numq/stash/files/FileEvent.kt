package com.numq.stash.files

sealed class FileEvent {
    object Empty : FileEvent()
    object Clear : FileEvent()
    object Refresh : FileEvent()
    data class File(val file: ImageFile) : FileEvent()
}