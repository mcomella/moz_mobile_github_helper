package org.mozilla.github_bot

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.todo

class AppTest {

    @Test
    fun test() = withTestApplication(Application::main) {
        with (handleRequest(HttpMethod.Get, "/")) {
            todo { "implement me!" }
        }
    }
}
