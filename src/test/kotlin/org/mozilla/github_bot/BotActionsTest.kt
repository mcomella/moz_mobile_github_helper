package org.mozilla.github_bot

import kotlinx.coroutines.experimental.runBlocking
import okhttp3.HttpUrl
import org.junit.Test

import org.junit.Before
import org.mockito.Mockito.*

private val TEST_URL = HttpUrl.parse("https://github.com")!!

class BotActionsTest {

    private lateinit var mockGithub: GithubAPIv3

    @Before
    fun setUp() {
        mockGithub = mock(GithubAPIv3::class.java)
    }

    @Test
    fun `test writeIssueNumsInPRComment success formatting`() = runBlocking {
        val initialComment = "This bug is about deleting the database. Good luck!"
        val expectedUpdate = """
            |${BotActions.BOT_PR_HEADER}
            |- [ ] #10
            |- [ ] #20
            |
            |---
            |
            |$initialComment
        """.trimMargin()

        BotActions.writeIssueNumsInPRComment(mockGithub, TEST_URL, sortedSetOf(20L, 10L), initialComment)
        verify(mockGithub).updatePR(TEST_URL, expectedUpdate)
        // todo: it'd be clearer/more flexible to assert a captured arg but it's not working in kotlin.
    }

    @Test
    fun `test writeIssueNumsInPRComment formatting for blank initial comment`() = runBlocking {
        BotActions.writeIssueNumsInPRComment(mockGithub, TEST_URL, sortedSetOf(30L, 20L), "    ")
        verify(mockGithub).updatePR(TEST_URL, """
            |${BotActions.BOT_PR_HEADER}
            |- [ ] #20
            |- [ ] #30
        """.trimMargin())
    }

    // todo: better way to fix matchers in kotlin?
    @Test
    fun `test writeIssueNumsInPRComment returns without request when initial comment already contains header`() = runBlocking {
        BotActions.writeIssueNumsInPRComment(mockGithub, TEST_URL, sortedSetOf(30L, 20L),
                "${BotActions.BOT_PR_HEADER}\n- [ ] $30")
        verify(mockGithub, never()).updatePR(any(HttpUrl::class.java) ?: TEST_URL, anyString() ?: "")
    }

    @Test
    fun `test writeIssueNumsInPRComment returns without request when there are no issue numbers`() = runBlocking {
        BotActions.writeIssueNumsInPRComment(mockGithub, TEST_URL, sortedSetOf(), "Whatever")
        verify(mockGithub, never()).updatePR(any(HttpUrl::class.java) ?: TEST_URL, anyString() ?: "")
    }
}