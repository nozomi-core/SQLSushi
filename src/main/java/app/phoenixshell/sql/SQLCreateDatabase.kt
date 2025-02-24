package app.phoenixshell.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

sealed class DatabaseMode {
    object Memory: DatabaseMode()
    object External: DatabaseMode()
}

fun createDatabase(
    targetVersion: Int,
    name: String,
    mode: DatabaseMode,
    connection: SQLDatabaseConnection,
    migrations: SQLDatabaseMigrationFactory? = null,
    engine: SQLDatabaseEngine? = null
): SQLDatabase {

    val options = SQLDatabaseOptions(
        targetVersion = targetVersion,
        name = name,
        mode = mode,
        connection = connection,
        migrations = migrations,
        engine = engine
    )

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = options.connection.createJdbcUrl(options)
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
                    factory = options.migrations,
                    currentVersion = currentVersion,
                    targetVersion = options.targetVersion
                )
            }
        }
    }
}

private fun runTargetMigrations(
    db: SQLDatabase,
    factory: SQLDatabaseMigrationFactory,
    currentVersion: Int,
    targetVersion: Int
) {
    val migrations = factory.onCreateMigrations()

    db.useTransaction { tact ->
        var version = currentVersion

        //Migrate from current version ie 0, and start to migrate from currentVersion + 1
        while(version < targetVersion) {
            val migrationVersion = ++version
            val nextMigration = migrations.find { it.version == migrationVersion }!!

            nextMigration.onMigrate(tact)
            nextMigration.onPostMigrate(tact)
        }

        db.setDatabaseVersion(tact, targetVersion)
    }.getOrThrow()
}