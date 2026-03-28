package app.phoenixshell.sql

data class LocalMigration(val version: Int, val callback: (tact: SQLTransaction) -> Unit)

class MigrationBuilder() {
    private val _migrationArray = mutableListOf<LocalMigration>()
    val migrationArray: List<LocalMigration>
        get() {
            return _migrationArray.sortedBy { it.version }
        }

    fun version(version: Int, callback: (tact: SQLTransaction) -> Unit) {
        _migrationArray.add(LocalMigration(version, callback))
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
                model.callback(tact)
            }
        }
    }

    return object : SQLDatabaseMigrationFactory {
        override fun onCreateMigrations(): Array<SQLDatabaseMigration> = mapMigration.toTypedArray()
    }
}