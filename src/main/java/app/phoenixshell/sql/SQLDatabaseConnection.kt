package app.phoenixshell.sql

import java.sql.Statement

interface SQLDatabaseConnection {
    fun createJdbcUrl(name: String, mode: DatabaseMode): String
    fun onCreateConnection(mode: DatabaseMode, stmt: Statement)
}