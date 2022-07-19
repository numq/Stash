package com.numq.stash.files

data class ImageFile(val extension: String, val blob: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageFile

        if (extension != other.extension) return false
        if (!blob.contentEquals(other.blob)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = extension.hashCode()
        result = 31 * result + blob.contentHashCode()
        return result
    }
}