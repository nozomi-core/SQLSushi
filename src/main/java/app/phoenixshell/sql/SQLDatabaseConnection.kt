package app.phoenixshell.sql

import java.sql.Connection

interface SQLDatabaseConnection {
    fun createJdbcUrl(options: SQLDatabaseOptions): String
    fun onCreateConnection(connection: Connection)
}