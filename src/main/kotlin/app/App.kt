package app

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

private const val PORT_PROPERTY = "server.port"

fun main(args: Array<String>) {
    val port = System.getProperty(PORT_PROPERTY).toInt()
    args.forEach { println(it) }
    // need to listen on differnt address

    val server = embeddedServer(Netty, port) {
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
        }
    }
    server.start(wait = true)
}
