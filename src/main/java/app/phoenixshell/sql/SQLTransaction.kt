package app.phoenixshell.sql

import java.sql.Connection

class SQLTransaction(
    private val connection: Connection
) {
    fun prepare(sql: String, fields: Array<String>): SQLPreparedStatement {
        return SQLPreparedStatement.create(connection, sql, fields)
    }

    fun exec(sql: String) {
        connection.createStatement().execute(sql)
    }
}