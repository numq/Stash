package com.numq.stash.extension

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatten

fun <T> Result<T>.toEither(): Either<Exception, T> =
    fold(onSuccess = ::Right, onFailure = ::Left).mapLeft(::Exception)

fun <T> Result<T>.toEither(condition: Boolean, exception: Exception): Either<Exception, T> =
    Either.conditionally(
        condition,
        ifFalse = { exception },
        ifTrue = { fold(onSuccess = ::Right, onFailure = ::Left).mapLeft(::Exception) }
    ).flatten()