package com.numq.stash.interactor

import android.util.Log
import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class UseCase<in T, R> {

    private val coroutineContext = Dispatchers.Default + Job()
    private val coroutineScope = CoroutineScope(coroutineContext)

    abstract fun execute(arg: T): Either<Exception, R>

    operator fun invoke(
        arg: T,
        onResult: (Either<Exception, R>) -> Unit
    ) {
        coroutineScope.launch {
            onResult(execute(arg).tap {
                Log.d(javaClass.simpleName, it.toString())
            })
        }
    }
}