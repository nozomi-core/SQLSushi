package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.sample.app.TestSchema
import org.junit.jupiter.api.Test

object LocalMigrationSuccess001: SQLDatabaseMigration {
    override val version: Int = 1

    override fun onMigrate(tact: SQLTransaction) {
        TestSchema.User.run {
            tact.exec("""
                CREATE TABLE success(a1 TEXT, a2 INTEGER);
            """.trimIndent())
        }
    }
}

object LocalMigrationSuccess002: SQLDatabaseMigration {
    override val version: Int = 2

    override fun onMigrate(tact: SQLTransaction) {
        TestSchema.User.run {
            tact.exec("""
                ALTER TABLE success
                ADD COLUMN a3 text;
            """.trimIndent())
        }
    }
}

object LocalMigrationSuccess: SQLDatabaseMigrationFactory {
    override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
        return arrayOf(
            LocalMigrationSuccess001,
            LocalMigrationSuccess002
        )
    }
}

class TestDatabaseMigration {

    @Test
    fun testMigration() {
        val db = createDatabase(
            targetVersion = 2,
            name = "migration-success.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = LocalMigrationSuccess,
            engine = DefaultSQLiteEngine
        )

        db.useTransaction {
            it.exec("INSERT INTO success(a1,a2,a3) values('title', 5, 'sample')")
        }.getOrThrow()
    }
}