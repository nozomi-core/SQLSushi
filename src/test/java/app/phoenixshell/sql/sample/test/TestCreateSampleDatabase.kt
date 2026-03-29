package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import app.phoenixshell.sql.data.ResultDecoder
import app.phoenixshell.sql.data.ResultDecoderNotImplemented
import app.phoenixshell.sql.query.insert
import app.phoenixshell.sql.query.select
import app.phoenixshell.sql.query.update
import app.phoenixshell.sql.sample.app.Tables
import app.phoenixshell.sql.sample.app.TestMigrations
import app.phoenixshell.sql.sample.app.TestModel
import app.phoenixshell.sql.sample.app.UserWhere
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.sql.ResultSet


@Serializable
data class UserModel(
    val name: String,
    val birthYear: Int?,
    val derived: Int?
)

@Serializable
data class NameUpdate(
    val name: String,
    val derived: Int
)


class TestCreateSampleDatabase {

    @Test
    fun testCreateDatabase() {

        val database = createDatabase(
            targetVersion = 1,
            name = "sample.db",
            mode = DatabaseMode.External,
            connection = DefaultSQLiteConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine,
            decoder = ResultDecoderNotImplemented
        )

        database.useWriteTransaction { context ->
            //context.exec(generateRandomInsert(Tables.User))

            val newName = NameUpdate("Popcorn", -1)

            val getUser = UserWhere.getBirthYear(78)
                .using(Tables.User)

            context.update(getUser, newName)
        }

        val result = database.useWriteTransaction { context ->
            val allUsers = UserWhere.getAll()
                .using(Tables.User)

            context.select<UserModel>(allUsers)
        }

        assertEquals(result.size, 4)



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
            engine = DefaultSQLiteEngine,
            decoder = ResultDecoderNotImplemented
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
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine,
            decoder = ResultDecoderNotImplemented
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
    fun testInsertDecode() {

        val localDecoder = object : ResultDecoder {
            override fun <T> decode(kClass: Class<T>, resultSet: ResultSet): T {
                return TestModel(
                    name = resultSet.getString("name"),
                    birthYear = resultSet.getInt("birthYear")
                ) as T
            }

        }

        val db = createDatabase(
            targetVersion = 1,
            name = "sample.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine,
            decoder = localDecoder
        )

        /*db.useTransaction { tact ->
            tact.insert(TestQuery.User.insert("Smith2", 99))
            tact.insert(TestQuery.User.insert("Example", 99))
        }

        val result = db.useTransaction { tact ->
           tact.query(TestQuery.User.getByAge(99)).decodeSingle<TestModel>()
        }

        assertEquals("Smith2", result.name)
        assertEquals("version=1", db.getDatabaseVersion().toString())*/
    }

    @Test
    fun testInsert100() {

        val db = createDatabase(
            targetVersion = 1,
            name = "insert100.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine,
            decoder = ResultDecoderNotImplemented
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
            mode = DatabaseMode.External,
            connection = DefaultSQLiteConnection,
            migrations = TestMigrations,
            engine = DefaultSQLiteEngine,
            decoder = ResultDecoderNotImplemented
        )

        val po = CompletableDeferred<String>()

        GlobalScope.launch {
            database.useWriteTransaction { context ->
                runBlocking {
                    context.exec("INSERT INTO users (name, birthYear, derived) VALUES ('John', 1990, 42);")
                    delay(7000)
                    po.complete("")
                }
            }
        }

        runBlocking {
            delay(200)
        }

        GlobalScope.launch {
            database.useWriteTransaction { context ->
                context.exec("INSERT INTO users (name, birthYear, derived) VALUES ('Sam', 2000, 21);")
            }
        }

        po.await()
    }
}