package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource
import java.sql.Statement

interface SQLDatabaseConnection: SQLModeSelector {
    fun createJdbcUrl(name: String, mode: DatabaseMode): String
    fun onCreateConnection(mode: DatabaseMode, stmt: Statement)
}

interface SQLModeSelector {
    fun onSelectDataSource(mode: DatabaseMode, connectionType: SQLConnectionMode, readSource: HikariDataSource, writeDataSource: HikariDataSource): HikariDataSource
}