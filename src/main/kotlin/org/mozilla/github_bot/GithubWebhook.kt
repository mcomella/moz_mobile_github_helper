package org.mozilla.github_bot

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import io.ktor.application.ApplicationCall
import io.ktor.content.readText
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.ValuesMap
import kotlinx.coroutines.experimental.async
import org.mozilla.github_bot.ext.parseRaw

private const val HEADER_GITHUB_EVENT = "X-Github-Event"

private interface GithubWebhookHandler {
    fun handle(call: ApplicationCall, payload: JsonObject)
}

enum class GithubWebhookType(val handler: GithubWebhookHandler) {
    PULL_REQUEST(GithubWebhookPullRequest);

    companion object {
        fun fromHeader(header: String?): GithubWebhookType? = when (header) {
            "pull_request" -> PULL_REQUEST
            else -> null
        }
    }
}

object GithubWebhook {
    suspend fun handle(call: ApplicationCall) {
        val payload = call.request.receiveContent().readText()
        if (!GithubWebhookAuth.isRequestAuthorized(EnvVar, call.request.headers, payload)) {
            call.respond(HttpStatusCode.Forbidden)
        } else {
            delegateRequestByType(call, payload)
        }
    }

    suspend fun delegateRequestByType(call: ApplicationCall, payload: String) {
        val requestType = GithubWebhook.getRequestType(call.request.headers)
        if (requestType == null) {
            println("Unknown github request type, ${GithubWebhook.getRequestTypeRaw(call.request.headers)}: unable to handle request.")
            call.respond(HttpStatusCode.NotImplemented, "Unknown request type")
            return
        }

        val payloadJSON = Parser().parseRaw(payload) as JsonObject?
        if (payloadJSON == null) {
            println("Unable to parse payload JSON: unable to handle request.")
            call.respond(HttpStatusCode.BadRequest, "Unable to parse payload JSON")
            return
        }

        requestType.handler.handle(call, payloadJSON)
        call.respond(HttpStatusCode.OK)
    }

    private fun getRequestTypeRaw(headers: ValuesMap) = headers.get(HEADER_GITHUB_EVENT)
    private fun getRequestType(headers: ValuesMap) = GithubWebhookType.fromHeader(getRequestTypeRaw(headers))
}

object GithubWebhookPullRequest : GithubWebhookHandler {
    override fun handle(call: ApplicationCall, payload: JsonObject) {
        val prAction = payload.string("action")
        when (prAction) {
            "opened" -> linkPRsToIssuesFromOpenedPayload(payload)
            else -> println("Do not handle PR action, $prAction: unable to handle request")
        }
    }

    // todo: testing? Here and above.
    fun linkPRsToIssuesFromOpenedPayload(payload: JsonObject) {
        val initialComment = payload.string("body") ?: ""
        println("lol $initialComment")
        // get body
        // get commits
        // search commits for issue #'s
        // Rewrite PR with Issue #'s (closes)
        // Rewrite Issues with PR #'s (closes)
    }
}