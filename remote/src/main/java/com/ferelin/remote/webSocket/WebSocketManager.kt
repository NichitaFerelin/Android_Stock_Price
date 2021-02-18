package com.ferelin.remote.webSocket

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val mDataToSubscribe: Collection<String>,
    private val mOnResponse: (response: String) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        mDataToSubscribe.forEach {
            webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$it\"}")
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        mOnResponse(text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        mOnResponse("")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.apply {
            close(WebSocketResponse.SOCKET_CLOSED, null)
            cancel()
        }
    }
}