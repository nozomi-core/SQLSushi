package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource
import java.sql.Statement

object DefaultSQLiteConnection: SQLDatabaseConnection {
    override fun createJdbcUrl(
        name: String,
        mode: DatabaseMode
    ): String {
        return if(mode is DatabaseMode.Memory) {
            "jdbc:sqlite::memory:"
        } else {
            "jdbc:sqlite:${name}"
        }
    }

    override fun onCreateConnection(mode: DatabaseMode, stmt: Statement) {
        stmt.execute("PRAGMA foreign_keys = ON;")
        stmt.execute("PRAGMA journal_mode = WAL;")
    }

    override fun onSelectDataSource(
        mode: DatabaseMode,
        connectionType: SQLConnectionMode,
        readSource: HikariDataSource,
        writeDataSource: HikariDataSource
    ): HikariDataSource {
        return if(mode == DatabaseMode.Memory) {
            writeDataSource
        } else {
            when(connectionType){
                SQLConnectionMode.WRITE -> writeDataSource
                SQLConnectionMode.READ -> readSource
            }
        }
    }
}