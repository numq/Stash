package com.numq.stash.file

interface File {
    companion object {
        const val NAME = "name"
        const val EXTENSION = "extension"
        const val BYTES = "bytes"
    }

    val name: String
    val extension: String
    val bytes: ByteArray
}