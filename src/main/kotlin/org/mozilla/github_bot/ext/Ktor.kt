package org.mozilla.github_bot.ext

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

// TODO: Does logging happen automatically?
suspend fun ApplicationCall.respondAndLog(statusCode: HttpStatusCode, message: Any = "") {
    println("$statusCode - $message")
    respond(statusCode, message)
}
