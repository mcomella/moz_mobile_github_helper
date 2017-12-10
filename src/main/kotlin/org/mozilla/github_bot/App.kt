package org.mozilla.github_bot

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.routing.post
import io.ktor.routing.routing

fun Application.main() {
    installFeatures()
    routing {
        post("/") {
            GithubWebhook.handle(call)
        }
    }
}

private fun Application.installFeatures() {
    install(DefaultHeaders)
    install(CallLogging)
}
