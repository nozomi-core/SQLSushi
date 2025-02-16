package app.phoenixshell.sql

class SQLDatabaseBuilder {
    var targetVersion: Int = 0
    var name: String? = null
    var inMemory = false
    var connection: SQLDatabaseConnection? = null
    var migrations: SQLDatabaseMigrationFactory? = null
    var engine: SQLDatabaseEngine? = null
}

class SQLDatabaseOptions(
    var targetVersion: Int,
    val name: String,
    val inMemory: Boolean,
    val connection: SQLDatabaseConnection,
    val migrations: SQLDatabaseMigrationFactory?,
    val engine: SQLDatabaseEngine?
)