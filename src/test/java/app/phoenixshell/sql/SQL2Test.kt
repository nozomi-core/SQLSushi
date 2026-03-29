package app.phoenixshell.sql

import app.phoenixshell.sql.sample.app.SampleQuery
import app.phoenixshell.sql.sample.app.Tables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SQL2Test {

    @Test
    fun testQuery2() {
        val query = SampleQuery
            .getUsers(9, "name")
            .using(Tables.User)

        assertEquals(query.statement.trim(), "select * from users where birthYear = ? and name = ?")

        assertEquals(9, query[0].value)
        assertEquals("name", query[1].value)
    }
}