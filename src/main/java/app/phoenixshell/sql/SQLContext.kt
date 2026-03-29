package app.phoenixshell.sql

import java.sql.Connection
import java.sql.PreparedStatement

class InternalSQLContext internal constructor(
    private val connection: Connection
): SQLContext {
    override fun exec(sql: String) {
        connection.createStatement().use { statement ->
            statement.execute(sql)
        }
    }

    override fun <T> prepare(sql: String, block: (PreparedStatement) -> T): T {
        return connection.prepareStatement(sql).use { stmt ->
            block(stmt)
        }
    }
}

interface SQLContext: SQLMigrationContext {
    override fun exec(sql: String)
    fun <T> prepare(sql: String, block: (PreparedStatement) -> T): T
}

interface SQLMigrationContext {
    fun exec(sql: String)
}