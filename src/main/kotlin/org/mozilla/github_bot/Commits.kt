package org.mozilla.github_bot

private val issueRegexes = arrayOf( // todo: constant
        """^Issue\s*#([0-9]+)""",
        """^Closes\s*#([0-9]+)""",
        """\(#([0-9]+)\)\s*.?$"""
).map { it.toRegex(RegexOption.IGNORE_CASE) }

object Commits { // todo: name.
    fun extractIssueNumFromCommitMsg(commitMsg: String) = issueRegexes.mapNotNull {
        it.find(commitMsg.trim())?.groupValues?.get(1)?.toLong()
    }.firstOrNull()
}


