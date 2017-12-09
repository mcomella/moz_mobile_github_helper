package org.mozilla.github_bot

import io.ktor.util.ValuesMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val HEADER_GITHUB_SECRET_SIG = "X-Hub-Signature"
private const val ENVVAR_GITHUB_SECRET = "GITHUB_SECRET"

private const val SIGNATURE_ALGORITHM = "HmacSHA1"

private val GITHUB_SECRET: String? = System.getenv(ENVVAR_GITHUB_SECRET) // todo: how mock?

// via https://developer.github.com/webhooks/securing/#validating-payloads-from-github
// Note: this could be built as a ktor feature.
fun doesGithubSecretMatch(headers: ValuesMap, payload: String): Boolean {
    val remoteSignature = headers.get(HEADER_GITHUB_SECRET_SIG)
    if (remoteSignature == null) {
        logFailure("Request missing header ${HEADER_GITHUB_SECRET_SIG}")
        return false
    } else if (GITHUB_SECRET == null) {
        logFailure("GITHUB_SECRET env var unavailable")
        return false
    }

    val localSignature = getLocalSignature(GITHUB_SECRET, payload)
    if ("sha1=$localSignature" != remoteSignature) {
        logFailure("Local signature does not match remote")
        return false
    }
    return true
}

private fun getLocalSignature(secret: String, payload: String): String {
    val keySpec = SecretKeySpec(secret.toByteArray(), SIGNATURE_ALGORITHM)
    val mac = Mac.getInstance(SIGNATURE_ALGORITHM)
    mac.init(keySpec)
    return mac.doFinal(payload.toByteArray()).toHex()
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
