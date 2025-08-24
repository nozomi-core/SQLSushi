package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource

class SQLConnection internal constructor(
    private val dataSource: HikariDataSource,
) {
    fun <T> useTransaction(transaction: (SQLTransaction) -> T): T {
        dataSource.connection.use { connection ->
            val transactionScope = SQLTransaction(connection)

            return try {
                val result = transaction(transactionScope)
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                e.printStackTrace()
                throw e
            } finally {
                transactionScope.close()
            }
        }
    }
}