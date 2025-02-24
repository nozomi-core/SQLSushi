package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.sample.app.TestMigrations
import app.phoenixshell.sql.sample.app.TestQuery
import app.phoenixshell.sql.sample.app.TestSchema
import app.phoenixshell.sql.sample.app.UserMapping
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TestCreateSampleDatabase {

    @Test
    fun testCreateDatabase() {

        val db = createDatabase(
            targetVersion = 1,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine
        )

        assertEquals("version=1", db.getDatabaseVersion().toString())
    }

    @Test
    fun testInsert() {

        val db = createDatabase(
            targetVersion = 1,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine
        )

        db.useTransaction { tact ->
            tact.insert(TestSchema.User, TestQuery.User.insert("Smith", 99))
        }

        val result = db.useTransaction { tact ->
            tact.query(TestSchema.User, TestQuery.User.getByAge(99)).map(UserMapping)
        }.getOrThrow()

        assertEquals("Smith", result[0].name)
        assertEquals("version=1", db.getDatabaseVersion().toString())
        assertEquals(1, result.size)
    }
}