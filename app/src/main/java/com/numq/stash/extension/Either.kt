package com.numq.stash.extension

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.numq.stash.action.CancellableAction

fun <L, R> Either<L, R>.action(): Either<L, CancellableAction> =
    CancellableAction.CANCELED.right().flatMap { this }.map { CancellableAction.COMPLETED }