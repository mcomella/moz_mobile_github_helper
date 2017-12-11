package org.mozilla.github_bot.ext

import com.beust.klaxon.Parser
import java.io.StringReader

/** Parses a JSON String. */
fun Parser.parseStr(stringValue: String) = parse(StringReader(stringValue))
