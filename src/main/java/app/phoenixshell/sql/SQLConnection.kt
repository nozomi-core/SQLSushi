package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource

class SQLConnection internal constructor(
    val writeDataSource: HikariDataSource
) {
    fun <T> useTransaction(transaction: (SQLContext) -> T): T {
        writeDataSource.connection.use { connection ->
            return try {
                val result = transaction(InternalSQLContext(connection))
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                e.printStackTrace()
                throw e
            }
        }
    }
}