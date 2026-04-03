package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLContext
import app.phoenixshell.sql.query

fun testUseQuery(sqlDatabase: SQLContext) {

}

object User {
    fun getBirthYear(xBirthYear: Int) = query<Tables.User> { sql ->
        sql.where {
            """
                where $birthYear = ${sql(birthYear, xBirthYear)}
            """
        }
    }

    fun getAll() = query<Tables.User> { sql ->
        sql.where {
            ""
        }
    }
}
