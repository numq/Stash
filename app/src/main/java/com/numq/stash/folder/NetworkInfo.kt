package com.numq.stash.folder

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun NetworkInfo(
    address: String,
    close: () -> Unit,
) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    DisposableEffect(Unit) {
        onDispose(close)
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .clickable(onClick = {
                        clipboardManager.setText(AnnotatedString(address))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                        close()
                    }), contentAlignment = Alignment.Center) {
                Row(
                    Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(address)
                    Icon(Icons.Rounded.ContentCopy, "copy address")
                }
            }
            IconButton(onClick = close) {
                Icon(Icons.Rounded.Close, "close network info")
            }
        }
    }
}