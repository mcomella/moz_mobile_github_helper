package org.mozilla.github_bot

import io.ktor.util.ValuesMapBuilder
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.todo

private const val HEADER_GITHUB_SECRET_SIG = "X-Hub-Signature"

internal class GithubWebhookAuthTest {

    // List of successful arguments.
    private val envVarWithSecret = mock(EnvVar::class.java).apply { `when`(githubSecret).thenReturn("localSecret") }
    private val headersWithRemoteSig =
            ValuesMapBuilder().apply { append(HEADER_GITHUB_SECRET_SIG, "sha1=signature") }.build()
    private val localRemoteSuccess = { _: String, _: String, _: String -> true }

    @Test
    fun `Request is unauthorized when headers are missing remote signature`() {
        val emptyHeaders = ValuesMapBuilder().build()
        assertFalse(GithubWebhookAuth.isRequestAuthorized(envVarWithSecret, emptyHeaders, "",
                localRemoteSuccess))
    }

    @Test
    fun `Request is unauthorized when github secret is missing from env vars`() {
        val envVarMissingGithubSecret = mock(EnvVar::class.java)
        `when`(envVarMissingGithubSecret.githubSecret).thenReturn("")
        assertFalse(GithubWebhookAuth.isRequestAuthorized(envVarMissingGithubSecret, headersWithRemoteSig, "",
                localRemoteSuccess))
    }

    @Test
    fun `Request is unauthorized when matching function fails`() {
        assertFalse(GithubWebhookAuth.isRequestAuthorized(envVarWithSecret, headersWithRemoteSig, "",
                { _, _, _ -> false}))
    }

    @Test
    fun `Request is authorized when github secret and remote signature header is present`() {
        assertTrue(GithubWebhookAuth.isRequestAuthorized(envVarWithSecret, headersWithRemoteSig, "",
                localRemoteSuccess))
    }

    @Test
    fun `Secrets do not match with random strings`() {
        assertFalse(GithubWebhookAuth.doSecretsMatch("aaa", "aaa", "aaa"))
        assertFalse(GithubWebhookAuth.doSecretsMatch("sha1=aoeu", "asdf", "lolol"))
    }

    @Test
    fun `Secrets match with known hardcoded match`() {
        todo { "Implement me to prevent regressions!" }
    }
}