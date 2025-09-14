package club.ctrl.server.server

import club.ctrl.server.entity.respondError
import club.ctrl.server.server.routes.authenticationRoutes
import club.ctrl.server.server.routes.challengesRoute
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.getValue

val dbModule = module {
    single {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString("mongodb://192.168.0.160:27017"))
            .build()
        val mongoClient = MongoClients.create(settings)
        mongoClient.getDatabase("ctrlclub_db")
    }
}

fun startServer() {
    val server = embeddedServer(Netty, port = 3000) {
        // koin for dependency injection (rn just the database)
        install(Koin) {
            slf4jLogger()
            modules(dbModule)
        }

        install(CORS) {
            allowHost("localhost:5173", schemes = listOf("http")) // dev server
            allowHost("ctrlclub.github.io", schemes = listOf("https")) // dev server
            allowCredentials = true
            allowHeader(HttpHeaders.ContentType)
        }

        // this adds support for json deserialization of request bodies into data classes iirc
        install(ContentNegotiation) { json() }

        // abort all requests on the AbortResponse exception, used for custom plugins
        install(StatusPages) {
            exception<AbortPipeline> { call, _ ->
                call.respondError("Error during authentication")
            }
        }

        // the actual routing happens here
        routing {
            val db by inject<MongoDatabase>()

            get("/") {
                call.respondText("OK");
            }

            route("/auth") {
                authenticationRoutes(db)
            }

            route("/challenges") {
                install(SIDValidator) {
                    dbHandle = db
                }

                challengesRoute(db)
            }
        }
    };

    server.start(true);
}