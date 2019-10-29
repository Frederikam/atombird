package com.frederikam.atombird.data

import com.google.gson.Gson
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.FluxSink
import java.util.concurrent.atomic.AtomicLong

class ClientSession(
        val email: String,
        private val raw: WebSocketSession,
        private val sink: FluxSink<WebSocketMessage>
) {
    companion object {
        private val gson = Gson();
        private val counter = AtomicLong(0);
    }

    val id = counter.getAndIncrement()

    fun send(message: WsEntity) {
        val str = gson.toJson(message)
        sink.next(raw.textMessage(str))
    }

    fun close(status: CloseStatus) = raw.close(status)

    override fun toString(): String {
        return "ClientSession(id='$id', email=$email)"
    }

}
