package app.phoenixshell.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

fun createDatabase(callback: SQLDatabaseBuilder.() -> Unit): SQLDatabase {
    val builder = SQLDatabaseBuilder()
    callback(builder)

    val options = SQLDatabaseOptions(
        builder.targetVersion,
        builder.name ?: "app.db",
        builder.inMemory,
        builder.connection!!,
        builder.migrations,
        builder.engine
    )

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = options.connection.createConnection(options)
        isAutoCommit = false
    }

    val dataSource = HikariDataSource(hikariConfig)

    dataSource.connection.use {
        options.connection.onCreateConnection(it)
    }

    val connection = SQLConnection(dataSource).apply {
        setupEngine(this, options)
    }
    return SQLDatabase(connection, options.engine).apply {
        setupMigrations(this, options)
    }
}

private fun setupEngine(db: SQLConnection, options: SQLDatabaseOptions) {
    val engine = options.engine
    val migrations = options.migrations

    if(engine != null && migrations != null) {
        val currentDatabase = engine.getCurrentDatabaseVersion(db)
        if(currentDatabase is SQLDatabaseVersion.EmptyVersion) {
            db.useTransaction {
                engine.onCreate(it)
            }
        }
    }
}

private fun setupMigrations(db: SQLDatabase, options: SQLDatabaseOptions) {
    val engine = options.engine
    val migrations = options.migrations

    if(engine != null && migrations != null) {
        val currentDatabase = db.getDatabaseVersion()
        if(currentDatabase is SQLDatabaseVersion.CurrentVersion) {
            val currentVersion = currentDatabase.version

            if(options.targetVersion >= 1) {
                runTargetMigrations(
                    db = db,
                    migrations = options.migrations,
                    currentVersion = currentVersion,
                    targetVersion = options.targetVersion
                )
            }
        }
    }
}

private fun runTargetMigrations(
    db: SQLDatabase,
    migrations: SQLDatabaseMigrationFactory,
    currentVersion: Int,
    targetVersion: Int) {

    db.useTransaction { tact ->
        var version = currentVersion

        //Migrate from current version ie 0, and start to migrate from currentVersion + 1
        while(version < targetVersion) {
            val newMigration = migrations.onCreateMigration(++version)
            newMigration.onMigrate(tact)
            newMigration.onPostMigrate(tact)
        }

        db.setDatabaseVersion(SQLDatabaseAdminPrivileges(migrations), tact, targetVersion)
    }.requireOkOrThrow()
}