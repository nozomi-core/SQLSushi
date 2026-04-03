package app.phoenixshell.sql

class InternalSQLDatabase internal constructor(
    private val writePool: SQLConnection,
    private val engine: SQLDatabaseEngine?
) {
    fun getDatabaseVersion() = useWriteTransaction { engine!!.getCurrentDatabaseVersion(it)  }
    internal fun setDatabaseVersion(context: SQLContext, version: Int) = engine!!.setCurrentDatabaseVersion(context, version)

    fun <T> useWriteTransaction(transaction:  (SQLContext) -> T): T = writePool.useTransaction(transaction)
}

