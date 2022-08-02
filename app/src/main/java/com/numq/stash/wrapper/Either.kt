package com.numq.stash.wrapper

import arrow.core.Either
import arrow.core.left
import arrow.core.right

fun <T> T.wrap(): Either<Exception, T> = runCatching { this }.fold({ it.right() },
    { Exception(it.localizedMessage).left() })