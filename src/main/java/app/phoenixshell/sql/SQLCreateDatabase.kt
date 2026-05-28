package app.phoenixshell.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.DriverManager

sealed class DatabaseMode {
    object Memory: DatabaseMode()
    object External: DatabaseMode()
}

fun createDatabase(
    targetVersion: Int,
    name: String,
    mode: DatabaseMode,
    connection: SQLDatabaseConnection,
    migrations: SQLDatabaseMigrationFactory,
    engine: SQLDatabaseEngine
): SQLDatabase {

    val databaseUrl = connection.createJdbcUrl(name, mode)
    setupConnection(databaseUrl,  mode, connection)

    val writeConfig = HikariConfig().apply {
        poolName = "WritePool"
        jdbcUrl = databaseUrl
        isAutoCommit = false
        maximumPoolSize = 1
        connectionTimeout = 500
    }

    val readConfig = HikariConfig().apply {
        poolName = "ReadPool"
        jdbcUrl = databaseUrl
        isAutoCommit = true
        maximumPoolSize = 8
        connectionTimeout = 500
    }

    val writeDataSource = HikariDataSource(writeConfig)
    val readDataSource = HikariDataSource(readConfig)

    return SQLDatabase(SQLConnection(
        writeDataSource = writeDataSource,
        readDataSource = readDataSource,
        modeSelector = connection,
        dbMode = mode
    ),
        engine = engine
    ).apply {
        setupEngine(this, engine)
        setupMigrations(this, targetVersion, migrations)
    }
}

private fun setupConnection(
    databaseUrl: String,
    mode: DatabaseMode,
    source: SQLDatabaseConnection
) {
    DriverManager.getConnection(databaseUrl).use { connection ->
        connection.createStatement().use { stmt ->
            source.onCreateConnection(mode, stmt)
        }
    }
}

private fun setupEngine(
    db: SQLDatabase,
    engine: SQLDatabaseEngine
) {
    db.useWriteTransaction { context ->
        val currentDatabase = engine.getCurrentDatabaseVersion(context)
        if(currentDatabase is SQLDatabaseVersion.EmptyVersion) {
            engine.onCreate(context)
        }
    }
}

private fun setupMigrations(
    db: SQLDatabase,
    targetVersion: Int,
    migrations: SQLDatabaseMigrationFactory
) {

    val currentDatabase = db.getDatabaseVersion()

    if(currentDatabase is SQLDatabaseVersion.CurrentVersion) {
        val currentVersion = currentDatabase.version

        if(targetVersion >= 1) {
            runTargetMigrations(
                db = db,
                factory = migrations,
                currentVersion = currentVersion,
                targetVersion = targetVersion
            )
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
    validateMigrations(migrations)

    var version = currentVersion

    //Migrate from current version ie 0, and start to migrate from currentVersion + 1
    while(version < targetVersion) {
        val migrationVersion = ++version
        val nextMigration = migrations.find { it.version == migrationVersion }!!

        db.useWriteTransaction { context ->
            nextMigration.onMigrate(context)
            db.setDatabaseVersion(context, targetVersion)
        }
    }
}

private fun validateMigrations(migrations: Array<SQLDatabaseMigration>) {
    migrations.forEachIndexed { index, migration ->
        if(migration.version != index + 1) {
            throw SQLMigrationException("Migration factory must return the migrations in the array in order of their version and no duplicate versions in the files")
        }
    }
}