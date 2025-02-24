package app.phoenixshell.sql

import java.sql.Connection

object DefaultSQLConnection: SQLDatabaseConnection {
    override fun createJdbcUrl(options: SQLDatabaseOptions): String {
        return if(options.mode is DatabaseMode.Memory) {
            "jdbc:sqlite::memory:"
        } else {
            "jdbc:sqlite:${options.name}"
        }
    }

    override fun onCreateConnection(connection: Connection) {
        connection.createStatement().execute("PRAGMA foreign_keys = ON;")
    }
}