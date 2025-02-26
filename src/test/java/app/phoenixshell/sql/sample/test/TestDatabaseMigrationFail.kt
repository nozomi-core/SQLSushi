package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.sample.app.TestSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

object LocalMigrationFail001: SQLDatabaseMigration {
    override val version: Int = 1

    override fun onMigrate(tact: SQLTransaction) {
        TestSchema.User.run {
            tact.exec("""
                create table first(title text);
            """.trimIndent())
        }
    }
}

object LocalMigrationFail002: SQLDatabaseMigration {
    override val version: Int = 1

    override fun onMigrate(tact: SQLTransaction) {
        TestSchema.User.run {
            tact.exec("""
                create table next(title text);
            """.trimIndent())
        }
    }
}

object LocalMigrationFailOrder: SQLDatabaseMigrationFactory {
    override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
        return arrayOf(
            LocalMigrationFail002,
            LocalMigrationFail001
        )
    }
}

object LocalMigrationFailSameVersionsDef: SQLDatabaseMigrationFactory {
    override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
        return arrayOf(
            LocalMigrationFail001,
            LocalMigrationFail002
        )
    }
}

class TestDatabaseMigrationFail {

    @Test
    fun testDatabaseMigrationFailOrderNoProperly() {
        val didFail = try {
            createDatabase(
                targetVersion = 1,
                name = "migration-fail-order.db",
                mode = DatabaseMode.Memory,
                connection = DefaultSQLConnection,
                migrations = LocalMigrationFailOrder,
                engine = DefaultSQLiteEngine
            )
            false
        } catch (e: SQLMigrationException) {
            true
        }

        assertTrue(didFail)
    }

    @Test
    fun testDatabaseMigrationDuplicateVersions() {
        val didFail = try {
            createDatabase(
                targetVersion = 1,
                name = "migration-fail-versions.db",
                mode = DatabaseMode.Memory,
                connection = DefaultSQLConnection,
                migrations = LocalMigrationFailSameVersionsDef,
                engine = DefaultSQLiteEngine
            )
            false
        } catch (e: SQLMigrationException) {
            true
        }

        assertTrue(didFail)
    }
}