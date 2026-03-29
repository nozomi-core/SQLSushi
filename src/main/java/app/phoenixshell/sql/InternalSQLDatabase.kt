package app.phoenixshell.sql

import java.sql.PreparedStatement

class InternalSQLDatabase internal constructor(
    private val writePool: SQLConnection,
    private val engine: SQLDatabaseEngine?
) {
    fun getDatabaseVersion() = useWriteTransaction { engine!!.getCurrentDatabaseVersion(it)  }
    internal fun setDatabaseVersion(context: SQLContext, version: Int) = engine!!.setCurrentDatabaseVersion(context, version)


    fun <T> useWriteTransaction(transaction:  (SQLContext) -> T): T = writePool.useTransaction(transaction)

        /*
    override fun prepare(sql: String): PreparedStatement {
        return writePool.writeDataSource.connection.use { connection ->
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        }
    }

    override fun exec(sql: String) {
        writePool.writeDataSource.connection.createStatement()

        writePool.writeDataSource.connection.use { connection ->
            connection.createStatement().execute(sql)
        }
    }*/
}

