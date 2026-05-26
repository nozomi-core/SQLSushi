package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.getAll
import app.phoenixshell.sql.getCreated
import app.phoenixshell.sql.query.delete
import app.phoenixshell.sql.query.asList
import app.phoenixshell.sql.query.insertAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestDeleteQuery {
    @Test
    fun testDeleteQuery() {
        val database = createSampleDatabase()

        val models = listOf(
            UserModel("123", "Sample", 84374L),
            UserModel("834", "Coffee", 12433L),
            UserModel("800", "Phone", 98376L)
        )

        database.useWriteTransaction { tact ->
            Tables.User.insertAll(tact, models)
        }

        database.useWriteTransaction { tact ->
            Tables.User.delete(tact, Tables.User.getCreated(84374L) )
        }

        val userNext = database.useReader {
            Tables.User
                .where(Tables.User.getAll())
                .asList<UserModel>(it)
        }

        val users = database.useReader { tact ->
            Tables.User
                .where(Tables.User.getAll())
                .asList<UserModel>(tact)
        }.map { it.name }

        Assertions.assertEquals(listOf("Coffee", "Phone"), users)
    }
}