package app.phoenixshell.sql

data class LocalMigration(val version: Int, val sql: String)

class MigrationBuilder() {
    private val _migrationArray = mutableListOf<LocalMigration>()
    val migrationArray: List<LocalMigration>
        get() {
            return _migrationArray.sortedBy { it.version }
        }

    fun version(version: Int, callback: () -> String) {
        _migrationArray.add(LocalMigration(version, callback()))
    }
}

fun buildMigrations(callback: MigrationBuilder.() -> Unit): SQLDatabaseMigrationFactory {
    val builder = MigrationBuilder()
    callback(builder)

    val mapMigration: List<SQLDatabaseMigration> = builder.migrationArray.map { model ->
        object: SQLDatabaseMigration {
            override val version: Int
                get() = model.version

            override fun onMigrate(tact: SQLTransaction) {
                tact.exec(model.sql)
            }
        }
    }

    return object : SQLDatabaseMigrationFactory {
        override fun onCreateMigrations(): Array<SQLDatabaseMigration> = mapMigration.toTypedArray()
    }
}