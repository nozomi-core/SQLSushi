package app.phoenixshell.sql

interface SQLDatabaseEngine {
    fun onCreate(database: SQLContext)
    fun getCurrentDatabaseVersion(database: SQLContext): SQLDatabaseVersion
    fun setCurrentDatabaseVersion(database: SQLContext, version: Int)
}