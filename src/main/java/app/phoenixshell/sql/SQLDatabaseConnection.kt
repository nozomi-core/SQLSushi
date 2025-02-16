package app.phoenixshell.sql

import java.sql.Connection

interface SQLDatabaseConnection {
    fun createConnection(options: SQLDatabaseOptions): String
    fun onCreateConnection(connection: Connection)
}