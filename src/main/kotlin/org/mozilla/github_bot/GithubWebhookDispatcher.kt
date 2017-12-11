package org.mozilla.github_bot

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.long
import com.beust.klaxon.obj
import com.beust.klaxon.string
import io.ktor.application.ApplicationCall
import io.ktor.content.readText
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.coroutines.experimental.async
import org.mozilla.github_bot.ext.parseRaw
import org.mozilla.github_bot.ext.respondAndLog

private const val HEADER_GITHUB_EVENT = "X-Github-Event"

private enum class GithubWebhookType {
    PULL_REQUEST;

    companion object {
        fun fromString(str: String) = when (str) {
            "pull_request" -> PULL_REQUEST
            else -> null
        }
    }
}

object GithubWebhookDispatcher {
    // If we wanted to make this more testable, we could have a registerListener call to decouple.
    suspend fun dispatch(call: ApplicationCall) {
        val payload = call.request.receiveContent().readText()
        if (!GithubWebhookAuth.isRequestAuthorized(EnvVar, call.request.headers, payload)) {
            call.respond(HttpStatusCode.Forbidden)

        } else {
            val webhookTypeRaw = call.request.headers.get(HEADER_GITHUB_EVENT) ?: ""
            val webhookType = GithubWebhookType.fromString(webhookTypeRaw)
            if (webhookType == null) {
                call.respondAndLog(HttpStatusCode.NotImplemented, "Unknown webhook type, $webhookTypeRaw")
            } else {
                dispatchByType(call, webhookType, payload)
            }
        }
    }

    private suspend fun dispatchByType(call: ApplicationCall, webhookType: GithubWebhookType, payload: String) {
        val payloadJSON = Parser().parseRaw(payload) as JsonObject?
        if (payloadJSON == null) {
            call.respondAndLog(HttpStatusCode.BadRequest, "Unable to parse payload JSON")
            return
        }

        // TODO: can execute requests concurrently? if so, no need for async. if so, need async.
        // TODO: async vs. run vs. launch
        when (webhookType) {
            GithubWebhookType.PULL_REQUEST -> GithubWebhookPullRequestDispatcher.dispatch(payloadJSON)
        }
        call.respondAndLog(HttpStatusCode.OK)
    }
}

object GithubWebhookPullRequestDispatcher {
    // todo: maybe extract common fields for handle? e.g. repo, owner, issue #
    fun dispatch(payload: JsonObject) {
        val prAction = payload.string("action")
        when (prAction) {
            "opened" -> linkPRsToIssuesFromOpenedPayload(payload)
            else -> println("Unable to respond to request: PR action, $prAction, not implemented")
        }
    }

    // todo: testing
    // todo: one function shoudl extract params from json, other fn with field should be in separate module.
    fun linkPRsToIssuesFromOpenedPayload(payload: JsonObject) {
        val pr = payload.obj("pull_request")
        if (pr == null) { println("Empty pull request: unable to handle request"); return }
        val initialComment = pr.string("body") ?: ""

        // dest vs. src
        val prNumber = pr.long("number")
        val prOwner = pr.string("owner")

        async { println("getting prs") }

        // get commits
        // search commits for issue #'s
        // Rewrite PR comment with Issue #'s (closes)
        // Rewrite Issues comment with PR #'s (closes)
    }
}