package com.numq.stash.websocket

data class SocketAddress(
    val protocol: String = "ws",
    val hostname: String = SocketClient.DEFAULT_HOSTNAME,
    val port: Int = SocketClient.DEFAULT_PORT,
) {
    override fun toString() = "%s://%s:%s".format(protocol, hostname, port)
}