package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource

class SQLConnection(
    private val dataSource: HikariDataSource,
) {
    fun <T> useTransaction(transaction: (SQLTransaction) -> T): SQLResult<T> {
        dataSource.connection.use { connection ->
            return try {
                val result = transaction(SQLTransaction(connection))
                connection.commit()
                SQLResult.Ok(result)
            } catch (e: Exception) {
                connection.rollback()
                e.printStackTrace()
                SQLResult.Fail(e)
            }
        }
    }

    fun query(sql: String, fields: Array<String>): SQLPreparedStatement {
        dataSource.connection.use {
            return SQLPreparedStatement.create(it, sql, fields)
        }
    }
}