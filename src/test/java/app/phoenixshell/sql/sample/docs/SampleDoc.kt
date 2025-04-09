package app.phoenixshell.sql.sample.docs

import app.phoenixshell.sql.*
import org.junit.jupiter.api.Test

object Schema: SQLSchema() {
    object User: SQLTableName(this, "user") {
        val firstName = string("first_name")
        val lastName = string("last_name")
        val createdAt = long("created_at")
    }
    object Posts: SQLTableName(this, "posts") {
        val title = string("title")
        val createdAt = long("created_at")
    }
}

object MyMigrations: SQLDatabaseMigrationFactory {
    override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
        return arrayOf(
            Migration1
        )
    }
}

object Migration1: SQLDatabaseMigration {
    override val version: Int = 1

    override fun onMigrate(tact: SQLTransaction) {
        with(Schema.User) {
            tact.exec("create table $table($firstName text, $lastName text, $createdAt integer);")
        }
    }
}

data class UserModel(val firstName: String, val lastName: String, val createdAt: Long)

val UserMapping: SQLMapper<Schema.User, UserModel> = {
    with(schema) {
        UserModel(
            firstName = get(firstName),
            lastName = get(lastName),
            createdAt = get(createdAt)
        )
    }
}

object UserQuery: SQLQueryList() {
    fun insert(vFirstName: String, vLastName: String) = buildQuery<Schema.User> { options, schema, statement, binding ->

        val vCreatedAt = System.currentTimeMillis()

        with(schema) {
            statement("""
                insert into $table(
                    $firstName,
                    $lastName,
                    $createdAt
                )
                values(
                    ${binding(firstName)},
                    ${binding(lastName)},
                    ${binding(createdAt)}
                );
            """).args(
                firstName maps vFirstName,
                lastName maps vLastName,
                createdAt maps vCreatedAt
            )
        }
    }

    fun findFirstName(vFirstName: String) = buildQuery<Schema.User> { _, schema, statement, binding ->
        with(schema) {
            statement("""
                select * from $table where $firstName = ${binding(firstName)}
            """).args(
                firstName maps vFirstName
            )
        }
    }
}


class SampleDoc {

    @Test
    fun testExample() {
        val db = createDatabase(
            targetVersion = 1,
            name = "sampledoc.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = MyMigrations,
            engine = DefaultSQLiteEngine
        )

        val insertUser = UserQuery.insert("MyFirstname", "MyLastname")

        db.useTransaction {
            it.insert(Schema.User, insertUser)
        }
    }

    @Test
    fun testExample2() {
        val db = createDatabase(
            targetVersion = 1,
            name = "sampledoc.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = MyMigrations,
            engine = DefaultSQLiteEngine
        )

        val findQuery = UserQuery.findFirstName("MyFirstname")

        val result =  db.useTransaction {
           it.query(Schema.User, findQuery, QueryOptions()).map(UserMapping)
        }.getOk()

        assert(result.isNotEmpty())
    }
}