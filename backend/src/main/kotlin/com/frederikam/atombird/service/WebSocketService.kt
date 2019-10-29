package com.frederikam.atombird.service

import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.ClientSession
import com.frederikam.atombird.data.WsEntity
import com.frederikam.atombird.data.findByTokenOrThrow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


@Service
class WebSocketService(private val accountRepository: AccountRepository) : WebSocketHandler {

    private val log: Logger = LoggerFactory.getLogger(WebSocketService::class.java)
    private val activeSessions = ConcurrentHashMap<String, Sessions>()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val token = session.handshakeInfo.headers.getFirst("Authorization")

        return accountRepository.findByTokenOrThrow(token).flatMap { account ->
            lateinit var clSession: ClientSession
            val input = session.receive().doOnNext { message ->
                session.textMessage("Echo $message")
            }

            val output = Flux.create<WebSocketMessage> { sink ->
                clSession = ClientSession(account.email, session, sink)
                onNewSession(account.email, clSession);
            }.doFinally {
                onClose(account.email, clSession)
            }

            return@flatMap Mono.zip(input.then(), output.then()).then()
        }
    }

    private fun onNewSession(email: String, session: ClientSession) {
        log.info("Accepted: {}", session)
        activeSessions.compute(email) { _, sessions ->
            if (sessions == null) {
                return@compute Sessions().apply { list.add(session) }
            } else {
                sessions.list.add(session)
                sessions
            }
        }
    }

    private fun onClose(email: String, session: ClientSession) {
        log.info("Closed: {}", session)
        activeSessions.computeIfPresent(email) { _, sessions ->
            sessions.list.remove(session);
            return@computeIfPresent if (sessions.list.isEmpty()) null else sessions
        }
    }

    private fun handleMessage(session: ClientSession, message: WebSocketMessage) {
        log.info("Client sent message: {}", message)
    }

    class Sessions {
        val list = CopyOnWriteArrayList<ClientSession>()

        fun sendAll(entity: WsEntity) = list.forEach { it.send(entity) }
        fun closeAll(status: CloseStatus): Mono<Void> {
            val ops = list.map { it.close(status) }.toTypedArray()
            return Flux.concatDelayError(*ops).then()
        }
    }
}