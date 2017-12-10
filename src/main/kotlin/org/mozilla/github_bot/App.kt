package org.mozilla.github_bot

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.readText
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.main() {
    installFeatures()
    routing {
        post("/") {
            val payload = call.request.receiveContent().readText()
            if (!GithubWebhookAuth.isRequestAuthorized(EnvVar, call.request.headers, payload)) {
                call.respond(HttpStatusCode.Forbidden)
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

private fun Application.installFeatures() {
    install(DefaultHeaders)
    install(CallLogging)
}
