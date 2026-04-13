package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.User
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.query.delete
import app.phoenixshell.sql.query.insertAll
import app.phoenixshell.sql.query.select
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
            tact.insertAll(Tables.User, models)
        }

        database.useWriteTransaction { tact ->
            val query = User.getCreatedAt(84374L)
                .using(Tables.User)

            tact.delete(query)
        }

        val users = database.useReader { tact ->
            val query = User.getAll()
                .using(Tables.User)

            tact.select<UserModel>(query)
        }.map { it.name }

        Assertions.assertEquals(listOf("Coffee", "Phone"), users)
    }
}