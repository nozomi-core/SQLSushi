package app.phoenixshell.sql

class SQLDatabase(
    private val connection: SQLConnection,
    private val engine: SQLDatabaseEngine?
) {
    fun <T> useTransaction(transaction: (SQLTransaction) -> T): SQLResult<T> = connection.useTransaction(transaction)
    fun getDatabaseVersion() = engine!!.getCurrentDatabaseVersion(connection)
    fun setDatabaseVersion(_admin: SQLDatabaseAdminPrivileges, transaction: SQLTransaction, version: Int) = engine!!.setCurrentDatabaseVersion(transaction, version)
    fun query(sql: String, fields: Array<SQLFieldName>) = connection.query(sql, fields)

    fun verifySetup(): SQLDatabase {
        val version = getDatabaseVersion()
        if(version == SQLDatabaseVersion.EmptyVersion) {
            throw Exception("sqlite database was not initialised")
        }
        return this
    }
}