package com.numq.stash.folder

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.numq.stash.websocket.SocketClient

@Composable
fun ConfigurationInput(lastAvailableAddress: String?, configure: (String?) -> Unit) {

    val qrScannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result?.contents?.let(configure)
        }
    )

    val scanQR = {
        val options = ScanOptions().apply {
            setBeepEnabled(false)
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setOrientationLocked(true)
            setPrompt("Scan a QR code")
        }
        qrScannerLauncher.launch(options)
    }

    val (addressInput, setAddressInput) = remember { mutableStateOf(lastAvailableAddress ?: "") }

    val isValidAddress by remember(addressInput) {
        derivedStateOf {
            addressInput.matches(Regex(SocketClient.REGEX_PATTERN))
        }
    }

    val close = { configure(addressInput.takeIf { isValidAddress }) }

    DisposableEffect(Unit) {
        onDispose(close)
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                addressInput,
                setAddressInput,
                placeholder = { Text("Type or paste your address here or scan QR code.") },
                isError = addressInput.isNotEmpty() && !isValidAddress,
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (addressInput.isEmpty()) close() else setAddressInput("") },
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Icon(Icons.Rounded.Clear, "clear input")
                        }
                        IconButton(onClick = close, enabled = isValidAddress) {
                            Icon(Icons.Rounded.Done, "apply input")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colors.background)
            )
            IconButton(onClick = { scanQR() }) {
                Icon(Icons.Rounded.QrCodeScanner, "launch qr code scanner")
            }
        }
    }
}