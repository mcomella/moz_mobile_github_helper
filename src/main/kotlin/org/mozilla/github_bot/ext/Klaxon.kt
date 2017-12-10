package org.mozilla.github_bot.ext

import com.beust.klaxon.Parser

/** Parses a JSON String. */
fun Parser.parseRaw(stringValue: String) = parse(StringBuilder(stringValue))
