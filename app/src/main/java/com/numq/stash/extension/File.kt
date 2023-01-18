package com.numq.stash.extension

import com.numq.stash.file.File

fun File.kindTitle() =
    if (name.length > 10) "${name.take(5)}...${name.takeLast(5)}.${extension}" else "${name}.${extension}"