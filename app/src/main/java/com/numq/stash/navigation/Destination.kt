package com.numq.stash.navigation

sealed class Destination private constructor(val name: String) {
    object Folder : Destination("folder")
}