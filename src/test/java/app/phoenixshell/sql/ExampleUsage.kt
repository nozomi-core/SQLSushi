package app.phoenixshell.sql

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.math.hypot

object Queries {
    object User: SQLQueryList() {
        fun maxAge(queryAge: Int) = buildQuery<Schema.Users> { selection, table, template ->
            template("")
                .bind {
            }
        }

        fun findAge(age: Int) = buildQuery<Schema.Users> { selection, table, template ->
            template("select $selection from ${table.tableName} where ${table.age} = ?")
                .bind {
                     set(table.age, age)
                }
        }

        fun longQuery(age: String) = buildQuery<LocationSchema> { selection, schema, template ->
            template(
                """
                    select $selection from ${schema.tableName} where ${schema.position} = ? 
                """
            ).bind {
                set(schema.position, age)
            }
        }
    }
}

class LocationSchema(
    val tableName: SQLTableName,
    val position: SQLFieldName<String>
) {
    companion object {
        fun from(users: Schema.Users) = LocationSchema(users, users.location)
    }
}

object Schema {
    object Users: SQLTableName("users") {
        val age = int("age")
        val isValid = boolean("is_valid")
        val location = string("position")
    }
}

data class SomeData(val age: Int, val isValid: Boolean)

val MyMapper: SQLMapper<Schema.Users, SomeData> = { schema, set ->
    with(set) {
        SomeData(
            age = unwrap(schema.age),
            isValid = unwrap(schema.isValid)
        )
    }
}


class ExampleUsage {

    @Test
    fun testClasses() {
        val age = 78

        val allow = when(age::class.java) {
            Int::class.java -> true
            else -> false
        }

        assertEquals(age::class.java, Int::class.java)
        assertTrue(allow)
    }

    @Test
    fun testString() {
        val age = ""

        val allow = when(age::class.java) {
            String::class.java -> true
            else -> false
        }

        assertEquals(age::class.java, String::class.java)
        assertTrue(allow)
    }

    @Test
    fun testDatabase() {

        val db = createDatabase(
            targetVersion = 1,
            name = "example.db",
            inMemory = true,
            connection = DefaultSQLConnection
        )





        db.useTransaction { tact ->
            with(Schema.Users) {
                tact.exec("create table $tableName($age integer, $isValid integer);")

                tact.prepare("insert into $tableName($age, $isValid) values(?, ?)", arrayOf(age, isValid)).apply {
                    set(age, 45)
                    set(isValid, true)
                }.execute()
            }
        }

        db.useTransaction { tact ->
            val queries = listOf(
                Queries.User.findAge(87),
                Queries.User.findAge(12),
            )

            val locationQuery = Queries.User.longQuery("example here")

            val hey = tact.perform(
                context = Schema.Users,
                query = queries,
                selection = {
                    arrayOf(it.location, it.age)
                }
            ).map { schema, set ->
                set.unwrap(schema.location)
            }

            val keys = tact.perform(
                context = LocationSchema.from(Schema.Users),
                query =  listOf(locationQuery),
                selection = { schema ->
                    arrayOf(schema.position)
                }
            )

        }


        db.useTransaction { tact ->
            val example = tact.prepare("select * from ${Schema.Users.tableName}", arrayOf()).executeQuery().from(MyMapper)
            example.forEach {
                println("MyAge: ${it.age}")
                ""
            }
        }
    }
}