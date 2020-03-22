package com.frederikam.atombird.data

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.FluxSink
import java.util.concurrent.atomic.AtomicLong

class UserSession(
        private val raw: WebSocketSession,
        private val sink: FluxSink<WebSocketMessage>
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserSession::class.java)
        private val gson = Gson();
        private val counter = AtomicLong(0);
    }

    lateinit var email: String
    var isAuthenticated = false
        private set
    val id = counter.getAndIncrement()

    fun send(response: Response) {
        check(isAuthenticated) { "This socket ($id) is not authenticated yet!" }

        val wrapper = WsWrapper(response.type.ordinal, gson.toJsonTree(response))
        sink.next(raw.textMessage(gson.toJson(wrapper)))
    }

    fun close(status: CloseStatus) = raw.close(status)

    fun onAuth(account: Account) {
        check(!isAuthenticated) { "This socket ($id) is already authenticated!" }
        email = account.email
        isAuthenticated = true
    }

    override fun toString(): String {
        return "ClientSession(id='$id')"
    }

}
