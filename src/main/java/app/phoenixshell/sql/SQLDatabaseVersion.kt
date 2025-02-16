package app.phoenixshell.sql

sealed class SQLDatabaseVersion {
    class CurrentVersion(val version: Int): SQLDatabaseVersion()
    object EmptyVersion: SQLDatabaseVersion()
}