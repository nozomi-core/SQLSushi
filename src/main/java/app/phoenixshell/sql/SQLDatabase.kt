package app.phoenixshell.sql

class SQLDatabase internal constructor(
    private val connection: SQLConnection,
    private val engine: SQLDatabaseEngine?
) {
    fun <T> useTransaction(transaction:  (SQLTransaction) -> T): SQLResult<T> = connection.useTransaction(transaction)
    fun getDatabaseVersion() = engine!!.getCurrentDatabaseVersion(connection)
    internal fun setDatabaseVersion(transaction: SQLTransaction, version: Int) = engine!!.setCurrentDatabaseVersion(transaction, version)

    fun verifySetup(): SQLDatabase {
        val version = getDatabaseVersion()
        if(version == SQLDatabaseVersion.EmptyVersion) {
            throw Exception("sqlite database was not initialised")
        }
        return this
    }
}