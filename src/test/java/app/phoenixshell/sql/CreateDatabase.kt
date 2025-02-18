package app.phoenixshell.sql

import org.junit.jupiter.api.Test

class CreateDatabase {

    class ExampleMigration : SQLDatabaseMigration {
        override val version = 1

        override fun onMigrate(tact: SQLTransaction) {
            tact.exec("create table user(age integer);")
        }

        override fun onPostMigrate(tact: SQLTransaction) {

        }
    }

    @Test
    fun testCreateDatabase() {
        val db = createDatabase(
            targetVersion = 1,
            name = ".test/app.db",
            inMemory = false,
            connection = DefaultSQLConnection,
            migrations = object : SQLDatabaseMigrationFactory {
                override fun onCreateMigration(targetVersion: Int): SQLDatabaseMigration {
                    return ExampleMigration()
                }
            }
        )
    }
}