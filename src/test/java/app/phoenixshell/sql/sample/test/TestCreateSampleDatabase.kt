package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.query.insert
import app.phoenixshell.sql.query.select
import app.phoenixshell.sql.query.update
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

@Serializable
data class NameUpdate(
    val name: String,
    val createdAt: Long
)

class TestCreateSampleDatabase {

    @Test
    fun testCreateDatabase() {
        val database = createSampleDatabase("create_database")

        database.useWriteTransaction { context ->
            val newName = NameUpdate("Popcorn", -1)
            val getUser = User.getCreatedAt(78)
                .using(Tables.User)

            context.update(getUser, newName)
            context.insert(Tables.User, UserModel("123", "example", System.currentTimeMillis()))
        }

        val result = database.useReader { context ->
            val allUsers = User.getAll()
                .using(Tables.User)

            context.select<UserModel>(allUsers)
        }

        assertEquals(result.size, 1)
        assertEquals("version=1", database.getDatabaseVersion().toString())
    }

    @Test
    fun testCreateAndBuildMigrations() {
        val db = createDatabase(
            targetVersion = 2,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
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

        db.useWriteTransaction { tact ->
            tact.exec("insert into example1(name, age) values ('Hello there', 81);")
        }
    }

    @Test
    fun testInsert() {
        val db = createDatabase(
            targetVersion = 1,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = SampleMigration,
            engine = DefaultSQLiteEngine
        )

        db.useWriteTransaction { tact ->
            //tact.insert(TestQuery.User.insert("Smith", 99))
            //tact.insert( TestQuery.User.insert("Example", 99))
        }

        val result = db.useWriteTransaction { tact ->
            //tact.query(TestQuery.User.getByAge(99), QueryOptions(limit = 1)).map(UserMapping)

        }

        //assertEquals("Smith", result[0].name)
        //assertEquals("version=1", db.getDatabaseVersion().toString())
        //assertEquals(1, result.size)
    }

    @Test
    fun testInsert100() {

        val db = createDatabase(
            targetVersion = 1,
            name = "insert100.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = SampleMigration,
            engine = DefaultSQLiteEngine
        )

        /*db.useTransaction { tact ->
            repeat(100) {


                tact.insert(TestQuery.User.insert(UUID.randomUUID().toString(), 99))
            }
        }

        val result = db.useTransaction { tact ->
            tact.query(TestQuery.User.getByAge(99), QueryOptions(limit = 5)).map(UserMapping)
        }

        assertEquals(5, result.size)*/
    }

    @Test
    fun testWriteTimeout() = runTest {

        val database = createDatabase(
            targetVersion = 1,
            name = "testWriteTimeout.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = SampleMigration,
            engine = DefaultSQLiteEngine
        )

        val po = CompletableDeferred<String>()

        GlobalScope.launch {
            database.useWriteTransaction { context ->
                runBlocking {
                    context.exec("INSERT INTO users (id, name, createdAt) VALUES ('09324', 'James', 42);")
                    delay(7000)
                    po.complete("")
                }
            }
        }

        runBlocking {
            delay(200)
        }

        assertThrows<Exception> {
            runBlocking { database.useWriteTransaction { context ->
                context.exec("INSERT INTO users (name, birthYear, derived) VALUES ('Sam', 2000, 21);")
            } }
        }

        po.await()
    }
}