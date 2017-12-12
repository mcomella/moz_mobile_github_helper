package org.mozilla.github_bot

import org.junit.Test

import org.junit.Assert.*

class GithubCommitsTest {

    @Test
    fun `Extracts issue number from "Issue #" prefix`() {
        val expectedIssueNum = 1776L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Issue #$expectedIssueNum: declared independence."))
    }

    @Test
    fun `Extracts issue number from "Closes #" prefix`() {
        val expectedIssueNum = 1787L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Closes #$expectedIssueNum: created a Constitution"))
    }

    @Test
    fun `Extracts issue number from "(#)" suffix`() {
        val expectedIssueNum = 1777L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Approve articles of confederation (#$expectedIssueNum)"))
    }

    @Test
    fun `Extracts issue number from "(#)" suffix with period`() {
        val expectedIssueNum = 1777L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Approve articles of confederation (#$expectedIssueNum)."))
    }

    @Test
    fun `Extracts issue number from "(#)" suffix with period and whitespace`() {
        val expectedIssueNum = 1777L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Approve articles of confederation (#$expectedIssueNum)  ."))
    }

    @Test
    fun `Extracts issue number while trimming whitespace`() {
        val expectedIssueNum = 1999L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "   Issue #1999: add readme    "))
    }

    @Test
    fun `Extracts issue number without whitespace between prefix and number`() {
        val expectedIssueNum = 1999L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Issue#1999: add readme"))
    }

    @Test
    fun `Extracts issue number with extra whitespace between prefix and number`() {
        val expectedIssueNum = 1999L
        assertEquals(expectedIssueNum, GithubCommits.extractIssueNumFromCommitMsg(
                "Issue      #1999: add readme"))
    }
}