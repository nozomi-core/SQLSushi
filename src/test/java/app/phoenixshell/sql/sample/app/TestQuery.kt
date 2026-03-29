package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLContext
import app.phoenixshell.sql.next.query

fun testUseQuery(sqlDatabase: SQLContext) {

}

object SampleQuery {
    fun getUsers(xBirthYear: Int, xName: String) = query<Tables.User> { sql ->
        sql.prepare {
            """
                select * from $table where $birthYear = ${sql(birthYear, xBirthYear)} and $name = ${sql(name, xName)}
            """
        }
    }
}
