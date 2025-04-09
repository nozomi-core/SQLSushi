# SQLSushi Usage
SQLSushi is a lightweight declarative functional approach to handling SQL queries and migrations in the JVM in a typesafe way without any code generation.
It works by defining everything at runtime so that no build step is required to bind the SQL queries in a typesafe manner

## 1. Define the Schema
To define the table schema, create a singleton and extend the `SQLSchema` class

The Standard convention is to use a singleton `Schema` object that contains all the field names of the SQL table names and column types.
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

## 2. Create the Migrations
Next step is to create the version migrations using the `SQLDatabaseMigrationFactory`.

You must supply and array with all the migration objects and the version numbers

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

## 3. Create the Database
The target version is the current version of the database. When you increment this value, the database will attempt to migrate the
database, based on the migrations provided
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

## 4. Create the queries
Once the database is setup, you can create and declare functional SQL Queries by creating an object that extends 
`SQLQueryList` and using the `buildQuery` method to define the SQL statement and any field bindings with their respective
values.

Note: The `binding` method creates the named placeholder for the field and the `args` method supplies the placeholder with the typed value.
If you call `binding` then there must be a call to `maps` in the args block, otherwise the statement can not be bound properly

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
Once the query statements are setup, you then have to create a mapping from the fields to the Data model like so:

Here you can the `get` method on an SQL field name to return its value.
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
You can then call the SQL queries like so:
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

## Advantages
The advantages of this library compared to other ones is that
1. It's lightweight and does not need code generation tooling
2. 100% flexible, because it takes a typesafe functional approach to defining queries without an ORM and directly mapping the data from the queries. No black magic behind the scenes
3. When you change the table schema, if there are any breaking changes to the queries, it won't compile the application since all methods are type safe and defined at compile time

## Disadvantages
1. Some syntax is verbose and adds more boilerplate because we don't use code generation and has to be typesafe
2. Migrations and queries still require testing because don't use ORM approach of using an entity model so the database schema may differ from the queries