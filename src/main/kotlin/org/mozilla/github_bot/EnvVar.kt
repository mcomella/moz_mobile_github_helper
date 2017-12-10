package org.mozilla.github_bot

private const val ENVVAR_GITHUB_SECRET = "GITHUB_SECRET"

/**
 * A collection of environment variables. Functions as a
 * wrapper around [System.getenv] for mocking purposes.
 */
object EnvVar {
    val githubSecret = System.getenv(ENVVAR_GITHUB_SECRET) ?: ""
}