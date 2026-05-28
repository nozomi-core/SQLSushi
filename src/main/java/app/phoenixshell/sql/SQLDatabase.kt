package app.phoenixshell.sql

class SQLDatabase internal constructor(
    private val writePool: SQLConnection,
    private val engine: SQLDatabaseEngine?
) {
    fun getDatabaseVersion() = useWriteTransaction { engine!!.getCurrentDatabaseVersion(it)  }
    internal fun setDatabaseVersion(context: SQLContext, version: Int) = engine!!.setCurrentDatabaseVersion(context, version)

    fun <T> useWriteTransaction(transaction:  (SQLContext) -> T): T = writePool.useWriter( transaction)
    fun <T> useReader(transaction:  (SQLReadContext) -> T): T = writePool.useReader(transaction)
}

