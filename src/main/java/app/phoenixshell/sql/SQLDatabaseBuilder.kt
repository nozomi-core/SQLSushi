package app.phoenixshell.sql

class SQLDatabaseOptions(
    var targetVersion: Int,
    val name: String,
    val mode: DatabaseMode,
    val connection: SQLDatabaseConnection,
    val migrations: SQLDatabaseMigrationFactory?,
    val engine: SQLDatabaseEngine?
)