package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.sample.app.TestMigrations
import app.phoenixshell.sql.sample.app.TestQuery
import app.phoenixshell.sql.sample.app.Tables
import app.phoenixshell.sql.sample.app.TestModel
import app.phoenixshell.sql.sample.app.UserMapping
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID

@Serializable
data class SimpleData(val text: String)

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
    fun testCreateAndBuildMigrations() {
        val db = createDatabase(
            targetVersion = 2,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = buildMigrations {
                version(1) { tact ->
                    tact.exec("""
                        create table example1(name text);
                    """)
                }
                version(2) { tact ->
                    tact.exec("""
                        ALTER TABLE example1 ADD COLUMN age INTEGER;
                    """)
                }
            },
            engine = DefaultSQLiteEngine
        )

        db.useTransaction { tact ->
            tact.exec("insert into example1(name, age) values ('Hello there', 81);")
        }
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
            tact.insert(Tables.User, TestQuery.User.insert("Smith", 99))
            tact.insert(Tables.User, TestQuery.User.insert("Example", 99))
        }

        val result = db.useTransaction { tact ->
            tact.query(Tables.User, TestQuery.User.getByAge(99), QueryOptions(limit = 1)).map(UserMapping)

        }

        assertEquals("Smith", result[0].name)
        assertEquals("version=1", db.getDatabaseVersion().toString())
        assertEquals(1, result.size)
    }

    @Test
    fun testInsertDecode() {

        val db = createDatabase(
            targetVersion = 1,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine
        )

        db.useTransaction { tact ->
            Tables.User.insert(tact, TestQuery.User.insert("Smith2", 99))
            Tables.User.insert(tact, TestQuery.User.insert("Example", 99))
        }

        val result = db.useTransaction { tact ->
            Tables.User.query(tact, TestQuery.User.getByAge(99)).decodeSingle<TestModel>()
        }

        assertEquals("Smith2", result.name)
        assertEquals("version=1", db.getDatabaseVersion().toString())
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


                tact.insert(Tables.User, TestQuery.User.insert(UUID.randomUUID().toString(), 99))
            }
        }

        val result = db.useTransaction { tact ->
            tact.query(Tables.User, TestQuery.User.getByAge(99), QueryOptions(limit = 5)).map(UserMapping)
        }

        assertEquals(5, result.size)
    }
}