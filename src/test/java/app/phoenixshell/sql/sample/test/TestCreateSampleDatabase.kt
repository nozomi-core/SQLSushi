package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.sample.app.TestMigrations
import app.phoenixshell.sql.sample.app.TestQuery
import app.phoenixshell.sql.sample.app.TestSchema
import app.phoenixshell.sql.sample.app.UserMapping
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID

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
            tact.insert(TestSchema.User, TestQuery.User.insert("Example", 99))
        }

        val result = db.useTransaction { tact ->
            tact.query(TestSchema.User, TestQuery.User.getByAge(99), QueryOptions(limit = 1)).map(UserMapping)
        }.getOrThrow()

        assertEquals("Smith", result[0].name)
        assertEquals("version=1", db.getDatabaseVersion().toString())
        assertEquals(1, result.size)
    }

    @Test
    fun testInsert100() {

        val db = createDatabase(
            targetVersion = 1,
            name = "insert100.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine
        )

        db.useTransaction { tact ->
            repeat(100) {
                tact.insert(TestSchema.User, TestQuery.User.insert(UUID.randomUUID().toString(), 99))
            }
        }

        val result = db.useTransaction { tact ->
            tact.query(TestSchema.User, TestQuery.User.getByAge(99), QueryOptions(limit = 5)).map(UserMapping)
        }.getOrThrow()

        assertEquals(5, result.size)
    }
}