package app.phoenixshell.sql.builder

import app.phoenixshell.sql.SQLSchema
import app.phoenixshell.sql.SQLTableName
import app.phoenixshell.sql.replaceAlphaNumVariables
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TestBuilder {

    @Test
    fun testFields() {
        val userTable = SQLTableName(SQLSchema(),"user")
        val field = userTable.string("age")
        val str = "$field"
        assertEquals("age", str)
    }

    @Test
    fun replace() {
        val userTable = SQLTableName(SQLSchema(), "user")
        val field = userTable.int("age")
        val stmt = "age = ?"
        replaceAlphaNumVariables(stmt, arrayOf(field))
    }
}