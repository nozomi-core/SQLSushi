package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.getAll
import app.phoenixshell.sql.query.asList
import app.phoenixshell.sql.query.insertAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestInsertDatabase {

    @Test
    fun testInsert() {
        val database = createSampleDatabase()

        val models = listOf(
            UserModel("123", "Sample", 84374L),
            UserModel("834", "Coffee", 12433L),
            UserModel("800", "Phone", 98376L)
        )

        database.useWriteTransaction { tact ->
            Tables.User.insertAll(tact, models)
        }


        val users = database.useReader { tact ->
            Tables.User
                .where(getAll())
                .asList<UserModel>(tact)
        }.map { it.name }

        Assertions.assertEquals(listOf("Sample", "Coffee", "Phone"), users)
    }
}