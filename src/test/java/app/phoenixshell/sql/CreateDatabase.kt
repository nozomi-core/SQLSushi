package app.phoenixshell.sql

import org.junit.jupiter.api.Test

class CreateDatabase {

    object ExampleMigration : SQLDatabaseMigration {
        override val version = 1

        override fun onMigrate(tact: SQLTransaction) {
            tact.exec("create table user(age integer);")
        }
    }

    @Test
    fun testCreateDatabase() {
        val db = createDatabase(
            targetVersion = 1,
            name = ".test/app.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = object : SQLDatabaseMigrationFactory {
                override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
                    return arrayOf(
                        ExampleMigration
                    )
                }
            }
        )
    }
}