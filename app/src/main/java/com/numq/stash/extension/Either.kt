package com.numq.stash.extension

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.numq.stash.action.CancellableAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

inline fun <reified R> catch(crossinline f: () -> R): Either<Exception, R> =
    runCatching(f).fold(::Right, ::Left).mapLeft { t -> Exception(t.message, t.cause) }

inline fun <reified R> catch(
    condition: Boolean,
    exception: Exception,
    crossinline f: () -> R
): Either<Exception, R> =
    if (condition) {
        runCatching(f).fold(::Right, ::Left).mapLeft { t -> Exception(t.message, t.cause) }
    } else exception.left()

suspend inline fun <reified R> catchAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline f: suspend () -> R
): Either<Exception, R> =
    runCatching {
        withContext(coroutineContext) {
            f()
        }
    }.fold(::Right, ::Left).mapLeft { t -> Exception(t.message, t.cause) }

suspend inline fun <reified R> catchAsync(
    condition: Boolean,
    exception: Exception,
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline f: suspend () -> R
): Either<Exception, R> =
    if (condition) {
        runCatching {
            withContext(coroutineContext) {
                f()
            }
        }.fold(::Right, ::Left).mapLeft { t -> Exception(t.message, t.cause) }
    } else exception.left()

fun <L, R> Either<L, R>.action(): Either<L, CancellableAction> =
    CancellableAction.CANCELED.right().flatMap { this }.map { CancellableAction.COMPLETED }