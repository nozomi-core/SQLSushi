package app.phoenixshell.sql

import com.zaxxer.hikari.HikariDataSource

class SQLConnection internal constructor(
    val writeDataSource: HikariDataSource,
    val readDataSource: HikariDataSource,
    val modeSelector: SQLModeSelector,
    val dbMode: DatabaseMode
) {
    private fun <T> useConnection(mode: SQLConnectionMode, transaction: (SQLContext) -> T): T {
        val dataSource = modeSelector.onSelectDataSource(
            dbMode,mode,
            readSource = readDataSource,
            writeDataSource = writeDataSource
        )

        dataSource.connection.use { connection ->
            return try {
                val result = transaction(SQLInternalContext(connection))
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                e.printStackTrace()
                throw e
            }
        }
    }

    fun <T> useWriter(transaction: (SQLContext) -> T): T {
        return useConnection(SQLConnectionMode.WRITE, transaction)
    }

    fun <T> useReader(transaction: (SQLReadContext) -> T): T {
       return useConnection(SQLConnectionMode.READ) { context ->
           transaction(context as SQLReadContext)
       }
    }
}

enum class SQLConnectionMode {
    READ, WRITE
}