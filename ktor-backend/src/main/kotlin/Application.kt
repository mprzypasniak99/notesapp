package com.example

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import com.example.models.dto.CreateNoteBody
import com.example.models.dto.GetNoteBody
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.util.logging.error
import java.util.Properties

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val database = Database(JdbcSqliteDriver(IN_MEMORY, Properties(), Database.Schema))
    install(ContentNegotiation) {
        json()
    }
    routing {

        get("/notes") {
            val dbNotes = database.noteQueries.selectAll().executeAsList()
            val dtoNotes = dbNotes.map { GetNoteBody(it.id, it.content, it.favourite) }
            call.respond(dtoNotes)
        }

        post<CreateNoteBody>("/note") { note ->
            database.noteQueries.insert(note.content)
            call.respond(HttpStatusCode.Created)
        }

        delete("/note/{id}") {
            val correctId = verifyCorrectNoteId() ?: return@delete
            val rowsDeleted = database.noteQueries.delete(correctId).await()
            if (rowsDeleted == 0L) {
                call.respond(HttpStatusCode.NotFound, "Note with id = $correctId not found")
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }

        put("/favourite/{id}") {
            val correctId = verifyCorrectNoteId() ?: return@put
            val rowsAdded = database.noteQueries.updateFavourite(1, correctId).await()
            if (rowsAdded == 0L) {
                call.respond(HttpStatusCode.BadRequest, "Failed to mark note with id = $correctId as favourite")
            } else {
                call.respond(HttpStatusCode.Created)
            }
        }

        delete("/favourite/{id}") {
            val correctId = verifyCorrectNoteId() ?: return@delete
            val rowsDeleted = database.noteQueries.updateFavourite(0, correctId).await()
            if (rowsDeleted == 0L) {
                call.respond(HttpStatusCode.NotFound, "Note with id = $correctId not found in favourites")
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

private suspend fun RoutingContext.verifyCorrectNoteId(): Long? {
    val id = call.parameters["id"]

    if (id == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing id")
        return null
    }

    val correctId = try {
        id.toLong()
    } catch (exception: NumberFormatException) {
        call.application.environment.log.error(exception)
        call.respond(HttpStatusCode.BadRequest, "Invalid id")
        return null
    }

    return correctId
}