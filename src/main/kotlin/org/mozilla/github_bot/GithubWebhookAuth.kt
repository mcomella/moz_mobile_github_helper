package org.mozilla.github_bot

import io.ktor.util.ValuesMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val HEADER_GITHUB_SECRET_SIG = "X-Hub-Signature"

private const val SIGNATURE_ALGORITHM = "HmacSHA1"

object GithubWebhookAuth {

    fun isRequestAuthorized(envVar: EnvVar, headers: ValuesMap, payload: String,
                            doSecretsMatch: (String, Secret: String, String) -> Boolean = this::doSecretsMatch // todo: type names?
    ): Boolean {
        val remoteSignature = headers.get(HEADER_GITHUB_SECRET_SIG)
        val localSecret = envVar.githubSecret
        if (remoteSignature == null) {
            logFailure("Request missing header ${HEADER_GITHUB_SECRET_SIG}")
            return false
        } else if (localSecret.isEmpty()) {
            logFailure("githubSecret env var unavailable")
            return false
        }

        val secretsMatch = doSecretsMatch(remoteSignature, localSecret, payload)
        if (!secretsMatch) {
            logFailure("Local signature does not match remote")
        }
        return secretsMatch
    }

    // via https://developer.github.com/webhooks/securing/#validating-payloads-from-github
    // Note: this could be built as a ktor feature.
    fun doSecretsMatch(remoteSignature: String, localSecret: String, payload: String): Boolean {
        val keySpec = SecretKeySpec(localSecret.toByteArray(), SIGNATURE_ALGORITHM)
        val mac = Mac.getInstance(SIGNATURE_ALGORITHM)
        mac.init(keySpec)
        val localSignature = mac.doFinal(payload.toByteArray()).toHex()
        return "sha1=$localSignature" == remoteSignature
    }
}

// via https://stackoverflow.com/a/15429408/2219998.
// May be slow, but I didn't want to take the time to convert the fast one
// into kotlin: https://stackoverflow.com/a/9855338/2219998
private fun ByteArray.toHex(): String {
    val builder = StringBuilder()
    forEach { builder.append(String.format("%02x", it)) }
    return builder.toString()
}

private fun logFailure(str: String) = println("Unable to authorize request: $str")
