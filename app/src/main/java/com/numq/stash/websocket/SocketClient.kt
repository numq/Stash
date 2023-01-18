package com.numq.stash.websocket

import android.util.Log
import com.numq.stash.extension.isSocketMessage
import com.numq.stash.extension.message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

interface SocketClient {
    companion object {
        const val ADDRESS_PATTERN = "ws://%s:%s"
    }

    val connectionState: StateFlow<ConnectionState>
    val messages: Channel<Message>
    suspend fun signal(message: Message)
    fun start()
    fun stop()

    class Implementation constructor(
        private val address: String
    ) : SocketClient {

        private val coroutineContext = Dispatchers.Default + Job()
        private val coroutineScope = CoroutineScope(coroutineContext)

        private var client: WebSocketClient? = null

        private fun createClient() =
            object : WebSocketClient(URI(address)) {

                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d(javaClass.simpleName, "Connected to server")
                    _connectionState.update { ConnectionState.CONNECTED }
                }

                override fun onMessage(message: String?) {
                    message?.takeIf { it.isSocketMessage }?.let {
                        Log.d(javaClass.simpleName, "Got message from server: ${it.take(50)}")
                        messages.trySend(it.message)
                    }
                }

                override fun onMessage(bytes: ByteBuffer?) {
                    bytes?.array()?.toString()?.takeIf { it.isSocketMessage }?.let {
                        Log.d(javaClass.simpleName, "Got message from server: ${it.take(50)}")
                        messages.trySend(it.message)
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    _connectionState.update { ConnectionState.DISCONNECTED }
                    Log.d(javaClass.simpleName, "Disconnected from server")
                    if (code != 1000) start()
                }

                override fun onError(e: Exception?) {
                    Log.e(javaClass.simpleName, e?.localizedMessage ?: "Socket error")
                }

                override fun reconnect() {
                    Log.d(javaClass.simpleName, "Client reconnecting")
                }
            }

        private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
        override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

        override val messages: Channel<Message> = Channel(Channel.UNLIMITED)

        override suspend fun signal(message: Message) {
            client?.run { if (isOpen) send(message.toString()) }
        }

        override fun start() {
            if (client != null) stop()
            _connectionState.update { ConnectionState.CONNECTING }
            client = createClient()
            coroutineScope.launch {
                delay(1000)
                try {
                    client?.connect()
                } catch (e: Exception) {
                    client?.reconnect()
                }
            }
        }

        override fun stop() {
            client?.close(1000)
            client = null
            _connectionState.update { ConnectionState.DISCONNECTED }
        }
    }
}