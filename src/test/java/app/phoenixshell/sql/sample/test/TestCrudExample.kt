package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.getAll
import app.phoenixshell.sql.query.UpdateEntity
import app.phoenixshell.sql.query.asList
import app.phoenixshell.sql.whereCreatedAt
import app.phoenixshell.sql.query.delete
import app.phoenixshell.sql.query.insert
import app.phoenixshell.sql.query.update
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import paginate

class TestCrudExample {

    @Test
    fun testInsert() {
        val database = createSampleDatabase("create_database")


        database.useWriteTransaction { tact ->
            with(Tables.User) {
                insert(tact, UserModel("123", "Sample", 84374L))
            }
        }
    }

    @Test
    fun testDelete() {
        val database = createSampleDatabase("create_database")

        database.useWriteTransaction { tact ->
            Tables.User.delete(tact, Tables.User.whereCreatedAt(84374L))
        }
    }

    @Serializable
    data class UpdateName(val name: String): UpdateEntity

    @Test
    fun testUpdate() {
        val database = createSampleDatabase("create_database")

        database.useWriteTransaction { tact ->
            with(Tables.User) {
                update(tact, whereCreatedAt(84374L), UpdateName("Update Name"))
            }
        }
    }

    @Test
    fun testQuery() {
        val database = createSampleDatabase("create_database")

        val result = database.useReader { tact ->
            Tables.User.whereCreatedAt(84374L)
                .using(Tables.User)
                .asList<UserModel>(tact)
        }
    }

    @Test
    fun testPagination() {
        val database = createSampleDatabase("create_database")

        val result = database.useReader { tact ->
            getAll()
                .using(Tables.User)
                .paginate(null, limit = 2, Tables.User.name, Tables.User.id)
                .execute<UserModel>(tact)
        }
    }
}