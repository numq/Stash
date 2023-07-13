package com.numq.stash.websocket

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
        const val REGEX_PATTERN =
            "(ws{1,2})://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})"
        const val DEFAULT_HOSTNAME = "127.0.0.1"
        const val DEFAULT_PORT = 9000
    }

    val connectionState: StateFlow<ConnectionState>
    val messages: Channel<Message>
    suspend fun signal(message: Message)
    fun startWithString(address: String?)
    fun startWithAddress(address: SocketAddress)
    fun stop()

    class Implementation : SocketClient {

        private val clientScope = CoroutineScope(Dispatchers.Default + Job())

        private var connectionJob: Job? = null
        private var client: WebSocketClient? = null

        private fun createClient(address: SocketAddress, onSuccess: () -> Unit) =
            object : WebSocketClient(URI.create(address.toString())) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    println("Connected to server")
                    onSuccess()
                }

                override fun onMessage(message: String?) {
                    message?.takeIf { it.isSocketMessage }?.let {
                        println("Got message from server: ${it.take(50)}")
                        messages.trySend(it.message)
                    }
                }

                override fun onMessage(bytes: ByteBuffer?) {
                    bytes?.array()?.toString()?.takeIf { it.isSocketMessage }?.let {
                        println("Got message from server: ${it.take(50)}")
                        messages.trySend(it.message)
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    println("Disconnected from server")
                    if (code == 1000) _connectionState.update { ConnectionState.Disconnected }
                    else reconnect()
                }

                override fun onError(e: Exception?) {
                    println("Client exception: ${e?.localizedMessage ?: "Socket error"}")
                }

                override fun reconnect() {
                    println("Client reconnecting")
                    connectToClient(address, onSuccess)
                }
            }

        private val _connectionState =
            MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
        override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

        override val messages: Channel<Message> = Channel(Channel.UNLIMITED)

        override suspend fun signal(message: Message) {
            client?.run { if (isOpen) send(message.toString()) }
        }

        private fun connectToClient(address: SocketAddress, callback: () -> Unit = {}) {
            clientScope.launch {
                connectionJob?.cancelAndJoin()
                connectionJob = clientScope.launch {
                    _connectionState.update { ConnectionState.Connecting }
                    client?.close()
                    client = createClient(address, callback)
                    client?.connect()
                }
            }
        }

        override fun startWithString(address: String?) {
            val socketAddress = address?.let { addr ->
                Regex(REGEX_PATTERN)
                    .matchEntire(addr)
                    ?.groups
                    ?.filterNotNull()
                    ?.drop(1)
                    ?.takeIf { it.size == 3 }
                    ?.runCatching {
                        val (protocol, hostname, port) = map { it.value }.toTypedArray()
                        SocketAddress(protocol, hostname, port.toInt())
                    }
                    ?.getOrNull()
            } ?: SocketAddress()
            connectToClient(socketAddress) {
                _connectionState.update { ConnectionState.Connected(socketAddress) }
            }
        }

        override fun startWithAddress(address: SocketAddress) {
            connectToClient(address) {
                _connectionState.update { ConnectionState.Connected(address) }
            }
        }

        override fun stop() {
            clientScope.launch {
                connectionJob?.cancelAndJoin()
                client?.closeConnection(1000, "closed by client")
            }
        }
    }
}