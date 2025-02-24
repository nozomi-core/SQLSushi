package app.phoenixshell.sql

sealed class SQLDatabaseVersion {
    class CurrentVersion(val version: Int): SQLDatabaseVersion() {
        override fun toString(): String = "version=$version"
    }
    object EmptyVersion: SQLDatabaseVersion() {
        override fun toString(): String = "version=empty"
    }
}