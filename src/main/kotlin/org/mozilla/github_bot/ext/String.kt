package org.mozilla.github_bot.ext

import okhttp3.HttpUrl

fun String?.toHttpUrl() = this?.let { HttpUrl.parse(it) } // todo: test.
