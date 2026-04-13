package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.User
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.query.insertAll
import app.phoenixshell.sql.query.select
import app.phoenixshell.sql.query.update
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestUpdateQuery {

    @Test
    fun testUpdateQuery() {
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
            val query = User.getCreatedAt(98376L)
                .using(Tables.User)

            tact.update(query, UserModel("45", "Over", 1000L))
        }

        val userList = database.useReader { tact ->
            val query = User.getAll()
                .using(Tables.User)

            tact.select<UserModel>(query)
        }

        val users = userList.map { it.name }

        Assertions.assertEquals(listOf("Sample", "Coffee", "Over"), users)
        Assertions.assertEquals(userList.last().id, "45")
        Assertions.assertEquals(userList.last().createdAt, 1000L)

    }
}