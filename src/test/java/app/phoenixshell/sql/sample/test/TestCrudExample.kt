package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.getCreated
import app.phoenixshell.sql.query.delete
import app.phoenixshell.sql.query.insert
import app.phoenixshell.sql.query.update
import org.junit.jupiter.api.Test

class TestCrudExample {

    @Test
    fun testInsert() {
        val database = createSampleDatabase("create_database")


        database.useWriteTransaction { tact ->
            val model = UserModel("123", "Sample", 84374L)
            Tables.User.insert(tact, model)
        }
    }

    @Test
    fun testDelete() {
        val database = createSampleDatabase("create_database")

        database.useWriteTransaction { tact ->
            Tables.User.delete(tact, Tables.User.getCreated(84374L))
        }
    }

    @Test
    fun testUpdate() {
        val database = createSampleDatabase("create_database")

        database.useWriteTransaction { tact ->
            with(Tables.User) {
                update(tact, Tables.User.getCreated(84374L), 900L)
            }
        }
    }

    @Test
    fun testQuery() {

    }
}