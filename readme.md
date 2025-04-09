# SQLSushi Usage
SQLSushi is a lightweight declarative functional approach to handling SQL queries and migrations in the JVM in a typesafe way without any code generation.
It works by defining everything at runtime so that no build step is required to bind the SQL queries in a typesafe manner

## 1. Define the Schema
The first step is to define the table schema as follows

````kotlin
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
````
The convention is to define a singleton `Schema` object that contains all the field names of the SQL table names and column types.

## 2. Create the Migrations

````kotlin
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
````
Next you must create a `SQLDatabaseMigrationFactory` which contains all the migration statements for each version.

## 3. Create the Database
````kotlin
val db = createDatabase(
    targetVersion = 1,
    name = "sample.db",
    mode = DatabaseMode.External,
    connection = DefaultSQLConnection,
    migrations = MyMigrations,
    engine = DefaultSQLiteEngine
)
````
The target version is the current version of the database. When you increment this value, the database will attempt to migrate the 
database, based on the migrations provided

## 4. Create the queries

````kotlin
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
````

## 5. Create the mapping
````kotlin
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
````


## 6. Use the queries
````kotlin
 val db = createDatabase(
    targetVersion = 1,
    name = "sampledoc.db",
    mode = DatabaseMode.External,
    connection = DefaultSQLConnection,
    migrations = MyMigrations,
    engine = DefaultSQLiteEngine
)
//Insert Query
val insertUser = UserQuery.insert("MyFirstname", "MyLastname")

val resultInsert = db.useTransaction {
    it.insert(Schema.User, insertUser)
}
        
//Find Query
val findQuery = UserQuery.findFirstName("MyFirstname")

val resultFind = db.useTransaction {
    it.query(Schema.User, findQuery, QueryOptions()).map(UserMapping)
}
````