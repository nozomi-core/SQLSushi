package app.phoenixshell.sql

import createCursor
import encodeCursor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import paginate

class SQL2Test {

    @Test
    fun testQuery2() {
        val query = Tables.User
            .getCreated(9)
            .using(Tables.User)

        assertEquals( "where createdAt = ?",query.statement.trim())

        assertEquals(9L, query[0].value)
    }

    @Test
    fun testQueryPage() {
        val cursor = createCursor(lastId = "0932", lastOrderBy = "jaems")

        val query = Tables.User.getCreated(9)
            .using(Tables.User)
            .paginate(cursor?.let { encodeCursor(it) }, 30, Tables.User.name, Tables.User.id)

        assertEquals("select * from users where createdAt = ? AND (name, id) > (?, ?) ORDER BY name ASC, id ASC LIMIT ?",query.statement.trim())
    }

    @Test
    fun testCursor() {

    }
}