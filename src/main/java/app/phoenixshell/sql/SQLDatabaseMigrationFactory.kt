package app.phoenixshell.sql

interface SQLDatabaseMigrationFactory {
    fun onCreateMigrations(): Array<SQLDatabaseMigration>
}