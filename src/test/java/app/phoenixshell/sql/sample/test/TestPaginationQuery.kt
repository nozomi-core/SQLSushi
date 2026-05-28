package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
import app.phoenixshell.sql.getAll
import app.phoenixshell.sql.getByName
import app.phoenixshell.sql.query.insertAll
import decodeCursor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import paginate

class TestPaginationQuery {

   @Test
   fun testPaginate() {
       val database = createSampleDatabase()

       val models = listOf(
           UserModel("123", "Sample", 84374L),
           UserModel("834", "Coffee", 12433L),
           UserModel("800", "Phone", 98376L) ,
           UserModel("321", "Last", 39274L)
       )

       database.useWriteTransaction { tact ->
           Tables.User.insertAll(tact, models)
       }

       val result = database.useWriteTransaction { tact ->
           getAll()
               .using(Tables.User)
               .paginate(null, limit = 2, Tables.User.name, Tables.User.id)
               .execute<UserModel>(tact)
       }

       val users = result.data.map { it.name }

       Assertions.assertEquals(listOf("Coffee", "Last"), users)
       val decodeCursor = decodeCursor(result.nextCursor!!)
       Assertions.assertEquals("321", decodeCursor?.id)
       Assertions.assertEquals("Last", decodeCursor?.orderVal)

       val resultNext = database.useWriteTransaction { tact ->
           getAll()
               .using(Tables.User)
               .paginate(result.nextCursor, limit = 2, Tables.User.name, Tables.User.id)
               .execute<UserModel>(tact)
       }

       val usersNext = resultNext.data.map { it.name }

       Assertions.assertEquals(listOf("Phone", "Sample"), usersNext)
       val decodeNextCursor = resultNext.nextCursor.let { decodeCursor(it!!) }
       Assertions.assertEquals("123", decodeNextCursor?.id)
       Assertions.assertEquals("Sample", decodeNextCursor?.orderVal)

       val resultEmpty = database.useWriteTransaction { tact ->
           getAll()
               .using(Tables.User)
               .paginate(resultNext.nextCursor, limit = 2, Tables.User.name, Tables.User.id)
               .execute<UserModel>(tact)
       }
       Assertions.assertTrue(resultEmpty.data.isEmpty())
   }

    @Test
    fun testPaginateCategory() {
        val database = createSampleDatabase()

        val models = listOf(
            UserModel("1", "Travel", 84374L),
            UserModel("2", "Coffee", 12433L),
            UserModel("3", "Phone", 98376L),
            UserModel("4", "Travel", 39274L),
            UserModel("5", "Travel", 39238L),
            UserModel("6", "Last", 92383273L),
        )

        database.useWriteTransaction { tact ->
            Tables.User.insertAll(tact, models)
        }

        val result = database.useWriteTransaction { tact ->
            Tables.User.getByName("Travel")
                .using(Tables.User)
                .paginate(null, limit = 2, Tables.User.name, Tables.User.id)
                .execute<UserModel>(tact)
        }

        val users = result.data.map { it.createdAt }

        Assertions.assertEquals(listOf(84374L, 39274L), users)
        val decodeCursor = decodeCursor(result.nextCursor!!)
        Assertions.assertEquals("4", decodeCursor?.id)
        Assertions.assertEquals("Travel", decodeCursor?.orderVal)

        val resultNext = database.useWriteTransaction { tact ->
            getAll()
                .using(Tables.User)
                .paginate(result.nextCursor, limit = 2, Tables.User.name, Tables.User.id)
                .execute<UserModel>(tact)
        }

        val usersNext = resultNext.data.map { it.createdAt }

        Assertions.assertEquals(listOf(39238L), usersNext)
        val decodeNextCursor = resultNext.nextCursor.let { decodeCursor(it!!) }
        Assertions.assertEquals("5", decodeNextCursor?.id)
        Assertions.assertEquals("Travel", decodeNextCursor?.orderVal)

        val resultEmpty = database.useWriteTransaction { tact ->
            getAll()
                .using(Tables.User)
                .paginate(resultNext.nextCursor, limit = 2, Tables.User.name, Tables.User.id)
                .execute<UserModel>(tact)
        }
        Assertions.assertTrue(resultEmpty.data.isEmpty())
    }
}