package com.numq.stash.file

data class DocumentFile(
    override val name: String,
    override val extension: String,
    override val bytes: ByteArray
) : File {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentFile

        if (name != other.name) return false
        if (extension != other.extension) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + extension.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}