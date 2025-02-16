package app.phoenixshell.sql

interface SQLDatabaseMigrationFactory {
    fun onCreateMigration(targetVersion: Int): SQLDatabaseMigration
}