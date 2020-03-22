package com.frederikam.atombird.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import org.springframework.web.reactive.socket.CloseStatus

val CLOSE_STATUS_AUTH_ERROR = CloseStatus(4000)

enum class Operation(val type: Class<*>) {
    AUTH_REQUEST(String::class.java),
    AUTH_SUCCESS(JsonNull::class.java)
}

class WsWrapper(private val o: Int, private val p: JsonElement) {
    val opEnum: Operation get() = Operation.values()[o]
    fun <T> parse(gson: Gson) = gson.fromJson<T>(p, opEnum.type)!!
}

abstract class Response(@Transient val type: Operation)

class AuthSuccess() : Response(Operation.AUTH_SUCCESS)
