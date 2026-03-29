package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLContext
import app.phoenixshell.sql.next.where

fun testUseQuery(sqlDatabase: SQLContext) {

}

object UserWhere {
    fun getBirthYear(xBirthYear: Int) = where<Tables.User> { sql ->
        sql.prepare {
            """
                where $birthYear = ${sql(birthYear, xBirthYear)}
            """
        }
    }

    fun getAll() = where<Tables.User> { sql ->
        sql.prepare {
            ""
        }
    }
}
