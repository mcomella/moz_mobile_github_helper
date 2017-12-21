package org.mozilla.github_bot

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_USER_AGENT = "User-Agent"

private val USER_AGENT = GithubAPIv3::class.qualifiedName as String // todo: app name?

// todo: free caching? logging interceptor? connection failure?
private val okHttpClient = OkHttpClient.Builder()
        .followRedirects(true) // Github can redirect us. todo: add link
        .build()

class GithubAPIv3(private val oauth2Token: String) {

    private val defaultReqBuilder: Request.Builder get() { // todo: okay as val?
        return Request.Builder()
                .addHeader(HEADER_AUTHORIZATION, "token $oauth2Token")
                .addHeader(HEADER_USER_AGENT, USER_AGENT)
    }

    suspend fun getCommits(url: HttpUrl): JsonArray<JsonObject?>? {
        // todo: throw errors or swallow to null?
        // TODO: handle errors: auth error, rate limit
        val request = defaultReqBuilder.get().url(url).build()
        try {
            okHttpClient.newCall(request).execute().use { res ->
                if (!res.isSuccessful) { // todo: verify content type.
                    return null // todo
                }

                return Parser().parse(res.body()!!.string()) as JsonArray<JsonObject?>? // todo: cast failure?
            }
        } catch (e: IOException) {
            // todo:  if the request could not be executed due to cancellation,
            // a connectivity problem or timeout. Because networks can fail during
            // an exchange, it is possible that the remote server accepted the
            // request before the failure.
        }

        return null
    }
}
