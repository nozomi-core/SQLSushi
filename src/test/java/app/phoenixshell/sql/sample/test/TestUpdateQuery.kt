package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserWhere
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.query.asList
import app.phoenixshell.sql.query.insertAll
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
            Tables.User.insertAll(tact, models)
        }

        database.useWriteTransaction { tact ->
            val query = UserWhere.getCreatedAt(98376L)


            Tables.User.update(tact, query, UserModel("45", "Over", 1000L))
        }

        val userList = database.useReader { tact ->
            Tables.User
                .where(UserWhere.getAll())
                .asList<UserModel>(tact)
        }

        val users = userList.map { it.name }

        Assertions.assertEquals(listOf("Sample", "Coffee", "Over"), users)
        Assertions.assertEquals(userList.last().id, "45")
        Assertions.assertEquals(userList.last().createdAt, 1000L)

    }
}