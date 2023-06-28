package com.numq.stash.folder

sealed class SharingStatus private constructor() {
    object Offline : SharingStatus()
    object Connecting : SharingStatus()
    data class Sharing(val address: String) : SharingStatus()
}