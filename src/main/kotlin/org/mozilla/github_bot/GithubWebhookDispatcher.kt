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
import org.mozilla.github_bot.ext.parseStr
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
    // This would also allow us to have multiple separated functions to handle a single request.
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
        val payloadJSON = Parser().parseStr(payload) as JsonObject? // todo: cast failure?
        if (payloadJSON == null) {
            call.respondAndLog(HttpStatusCode.BadRequest, "Unable to parse payload JSON")
            return
        }

        when (webhookType) {
            GithubWebhookType.PULL_REQUEST -> GithubWebhookPullRequestDispatcher.dispatch(call, payloadJSON)
        }
    }
}

private object GithubWebhookPullRequestDispatcher {
    suspend fun dispatch(call: ApplicationCall, payload: JsonObject) {
        val prAction = payload.string("action")
        when (prAction) {
            "opened" -> dispatchPROpened(call, payload)
            else -> call.respondAndLog(HttpStatusCode.NotImplemented, "PR action, $prAction, not implemented")
        }
    }

    // todo: testing
    suspend fun dispatchPROpened(call: ApplicationCall, payload: JsonObject) {
        val pr = payload.obj("pull_request")
        val prNumber = pr?.long("number")

        val repo = payload.obj("repository")
        val repoName = repo?.string("name")
        val repoOwner = repo?.obj("owner")?.string("login")

        if (prNumber == null ||
                repoName == null ||
                repoOwner == null) {
            call.respondAndLog(HttpStatusCode.BadRequest, "Pull request is missing expected fields")
            return
        }

        val initialComment = pr.string("body") ?: ""

        // TODO: can execute requests concurrently? if so, no need for async. if so, need async. serially.
        // TODO: async vs. run vs. launch
        call.respondAndLog(HttpStatusCode.OK)
        linkIssuesToPRs(repoOwner = repoOwner, repoName = repoName, prNumber = prNumber, initialComment = initialComment)
    }

    // todo: move to separate module
    suspend fun linkIssuesToPRs(repoOwner: String, repoName: String, prNumber: Long, initialComment: String) {
        println("linking")
        // get new commits from pull_request/head/repo
        // search commits for issue #'s

        // Rewrite current repo PR with Issue #'s (closes)
        // Rewrite current repo Issues comment with PR #'s (closes)
    }
}
