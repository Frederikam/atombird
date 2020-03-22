package com.frederikam.atombird.service

import com.frederikam.atombird.data.AccountRepository
import com.frederikam.atombird.data.AuthSuccess
import com.frederikam.atombird.data.CLOSE_STATUS_AUTH_ERROR
import com.frederikam.atombird.data.UserSession
import com.frederikam.atombird.data.Operation
import com.frederikam.atombird.data.WsWrapper
import com.google.common.collect.ArrayListMultimap
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class WebSocketService(private val accountRepository: AccountRepository) : WebSocketHandler {

    private val log: Logger = LoggerFactory.getLogger(WebSocketService::class.java)
    private val gson = Gson()
    private val activeSessions = ArrayListMultimap.create<String, UserSession>()

    override fun handle(session: WebSocketSession): Mono<Void> {
        lateinit var clSession: UserSession
        val input = session.receive().doOnNext { message ->
            if (clSession.isAuthenticated) {
                handleMessage(clSession, message)
            } else {
                try {
                    handlePreAuthMessage(clSession, message)
                } catch (e: Exception) {
                    log.error("WS login failed", e)
                    session.close(CLOSE_STATUS_AUTH_ERROR).subscribe()
                }
            }
        }.doOnError { e ->
            log.error("Error handling WS message", e)
            session.close(CloseStatus.SERVER_ERROR).subscribe()
        }

        val output = Flux.create<WebSocketMessage> { sink ->
            clSession = UserSession(session, sink)
        }

        return session.send(output)
                .and(input)
                .doFinally { onClose(clSession) }
    }

    private fun handlePreAuthMessage(session: UserSession, message: WebSocketMessage) {
        val wrapper = gson.fromJson(message.payloadAsText, WsWrapper::class.java)
        check(wrapper.opEnum == Operation.AUTH_REQUEST) {
            "Expected ${Operation.AUTH_REQUEST} but got ${wrapper.opEnum} during pre-auth"
        }
        val token = wrapper.parse<String>(gson)
        accountRepository.findByToken(token).doOnError {
            log.error("WS login failed", it)
            session.close(CLOSE_STATUS_AUTH_ERROR).subscribe()
        }.subscribe {
            session.onAuth(it)
            onSessionAuth(session)
        }
    }

    private fun onSessionAuth(session: UserSession) {
        log.info("Accepted: {}", session)
        activeSessions.put(session.email, session)
        session.send(AuthSuccess())
    }

    private fun onClose(session: UserSession) {
        log.info("Closed: {}", session)
        if (!session.isAuthenticated) return
        activeSessions.remove(session.email, session)
    }

    private fun handleMessage(session: UserSession, message: WebSocketMessage) {
        log.info("Client sent message: {}", message)
    }
}