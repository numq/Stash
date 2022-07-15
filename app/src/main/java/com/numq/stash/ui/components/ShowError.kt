package com.numq.stash.home

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ShowError(
    scaffoldState: ScaffoldState,
    exception: Exception,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onErrorShown: () -> Unit = {}
) {
    exception.localizedMessage?.let {
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
        }.invokeOnCompletion {
            onErrorShown()
        }
    }
}