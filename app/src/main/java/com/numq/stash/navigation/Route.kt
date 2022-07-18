package com.numq.stash.navigation

import androidx.navigation.NamedNavArgument

sealed class Route private constructor(
    val name: String,
    val destination: String = name.lowercase(),
    val args: List<NamedNavArgument> = emptyList()
) {

    private companion object {
        const val FILES = "files"
    }

    object Files : Route(FILES)
}