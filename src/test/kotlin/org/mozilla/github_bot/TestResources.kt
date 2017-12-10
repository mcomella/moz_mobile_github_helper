package org.mozilla.github_bot

import io.ktor.util.ValuesMap
import io.ktor.util.ValuesMapBuilder
import java.io.File

/** A reference to loaded resources in "src/test/resources". */
object TestResources {
    val dir = File("src/test/resources")

    private val githubWebhooksDir = File(dir, "github_webhooks")

    private val spoonKnifePingDir = File(githubWebhooksDir, "spoon-knife-ping")
    val spoonKnifePing by lazy { GithubTestWebhook(
            headers = File(spoonKnifePingDir, "headers").readHeaders(),
            payload = File(spoonKnifePingDir, "payload.json").readTextAndRemoveSuffixNewline(),
            localSecret = File(spoonKnifePingDir, "local_secret").readTextAndRemoveSuffixNewline()
    )}
}

/**
 * The components of a github webhook. Note that headers and payload must not have
 * a newline at the end of the file to be identical to the received request.
 */
data class GithubTestWebhook(val headers: ValuesMap, val payload: String, val localSecret: String)

// An ext fn for consistency with loading other ping values with File.readText().
private fun File.readHeaders() = ValuesMapBuilder().apply {
    forEachLine {
        val headerAndValue = it.split(":", limit = 2)
        append(headerAndValue[0], headerAndValue[1].trim())
    }
}.build().also { print(it) }

// In order for these values to be identical to the requests, they cannot contain a newline character at the end.
private fun File.readTextAndRemoveSuffixNewline(): String = readText().removeSuffix("\n")
