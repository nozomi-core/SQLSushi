package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.User
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.createSampleDatabase
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
           tact.insertAll(Tables.User, models)
       }

       val result = database.useWriteTransaction { tact ->
           val query = User.getAll()
               .using(Tables.User)
               .paginate(null, limit = 2, Tables.User.name, Tables.User.id)


           query.execute<UserModel>(tact)
       }

       val users = result.data.map { it.name }

       Assertions.assertEquals(listOf("Coffee", "Last"), users)
       val decodeCursor = result.nextCursor.let { decodeCursor(it!!) }
       Assertions.assertEquals("321", decodeCursor?.id)
       Assertions.assertEquals("Last", decodeCursor?.orderVal)

       val resultNext = database.useWriteTransaction { tact ->
           val query = User.getAll()
               .using(Tables.User)
               .paginate(result.nextCursor, limit = 2, Tables.User.name, Tables.User.id)

           query.execute<UserModel>(tact)
       }

       val usersNext = resultNext.data.map { it.name }

       Assertions.assertEquals(listOf("Phone", "Sample"), usersNext)
       val decodeNextCursor = resultNext.nextCursor.let { decodeCursor(it!!) }
       Assertions.assertEquals("123", decodeNextCursor?.id)
       Assertions.assertEquals("Sample", decodeNextCursor?.orderVal)

       val resultEmpty = database.useWriteTransaction { tact ->
           val query = User.getAll()
               .using(Tables.User)
               .paginate(resultNext.nextCursor, limit = 2, Tables.User.name, Tables.User.id)

           query.execute<UserModel>(tact)
       }
       Assertions.assertTrue(resultEmpty.data.isEmpty())
   }
}