package app.phoenixshell.sql

interface SQLDatabaseEngine {
    fun onCreate(transaction: SQLTransaction)
    fun getCurrentDatabaseVersion(database: SQLConnection): SQLDatabaseVersion
    fun setCurrentDatabaseVersion(transaction: SQLTransaction, version: Int)
}