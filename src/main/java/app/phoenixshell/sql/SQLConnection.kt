package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource

class SQLConnection(
    private val dataSource: HikariDataSource,
) {
    fun <T> useTransaction(transaction: (SQLTransaction) -> T): SQLResult<T> {
        dataSource.connection.use { connection ->
            val transactionScope = SQLTransaction(connection)

            return try {
                val result = transaction(transactionScope)
                connection.commit()
                SQLResult.Ok(result)
            } catch (e: Exception) {
                connection.rollback()
                e.printStackTrace()
                SQLResult.Fail(e)
            } finally {
                transactionScope.close()
            }
        }
    }
}