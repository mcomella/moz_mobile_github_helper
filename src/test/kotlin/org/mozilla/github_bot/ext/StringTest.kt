package org.mozilla.github_bot.ext

import okhttp3.HttpUrl
import org.junit.Test

import org.junit.Assert.*

class StringTest {

    @Test
    fun `toHttpUrl on null receiver returns null`() {
        val nullStr: String? = null
        assertNull(nullStr.toHttpUrl())
    }

    @Test fun `toHttpUrl on empty str returns null`() = assertNull("".toHttpUrl())
    @Test fun `toHttpUrl on invalid URL returns null`() = assertNull("aoeu aoeu aoeu".toHttpUrl())

    @Test
    fun `toHttpUrl on valid URL returns HttpUrl#toParse`() {
        val validURLs = listOf(
                "http://mozilla.org",
                "https://github.com/mozilla-mobile/focus-android",
                "https://github.com:4040/mozilla-mobile/focus-ios/README.md",
                "file:///Users/octocat/Documents/"
        )

        validURLs.forEach {
            val expectedHttpUrl = HttpUrl.parse(it)
            assertEquals(expectedHttpUrl, it.toHttpUrl())
        }
    }
}
