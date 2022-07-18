package com.numq.stash.files

data class ImageFile(val blob: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageFile

        if (!blob.contentEquals(other.blob)) return false

        return true
    }

    override fun hashCode(): Int {
        return blob.contentHashCode()
    }
}