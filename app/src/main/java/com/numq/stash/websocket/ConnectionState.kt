package com.numq.stash.websocket

sealed class ConnectionState private constructor() {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(val address: SocketAddress) : ConnectionState()
}