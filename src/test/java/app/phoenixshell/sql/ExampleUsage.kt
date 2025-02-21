package app.phoenixshell.sql

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

object Queries {
    object User: SQLQueryList() {
        val findLocation: (String) -> Unit = {

        }

        fun findAge(ageVal: Int) = buildQuery<Schema.Users> { selection, table, template ->
            with(table) {
                template("select $selection from $tableName where $age = ?")
                    .bind(
                        age binds ageVal
                    )
            }
        }

        fun likeDescription(likeVal: String) = buildQuery<Schema.Popcorn> { selection, table, template ->
            with(table) {
                template("select * from $table where $desc = ? and example = ?")
                    .bind(
                        desc binds likeVal,
                        desc binds likeVal
                    )
            }
        }

    }
}

object Schema {
    object Users: SQLTableName("users") {
        val name = string("name")
        val age = int("age")
        val isValid = boolean("is_valid")
    }

    object Popcorn: SQLTableName("popcorn") {
        val desc = string("hello")
    }
}

data class SomeData(val age: Int, val isValid: Boolean)

val MyMapper: SQLMapper<Schema.Users, SomeData> = { schema, set ->
    with(set) {
        SomeData(
            age = fetch(schema.age),
            isValid = fetch(schema.isValid)
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

        Queries.User.findLocation("example")
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
                tact.exec("create table if not exists $tableName($name text, $age integer, $isValid integer);")

                tact.prepare("insert into $tableName($name, $age, $isValid) values(?, ?, ?)", arrayOf(name, age, isValid)).apply {
                    set(name, "John")
                    set(age, 45)
                    set(isValid, true)
                }.execute()

                tact.prepare("insert into $tableName($name, $age, $isValid) values(?, ?, ?)", arrayOf(name, age, isValid)).apply {
                    set(name, "Jess")
                    set(age, 67)
                    set(isValid, false)
                }.execute()
            }
        }.getOrThrow()


        val findQuery = Queries.User.findAge(45)

        val props = db.useTransaction { tact ->
            tact.query(
                context = Schema.Users,
                query = findQuery,
                selection = {
                    arrayOf(it.name, it.age)
                }
            ).map { schema, set ->
                Pair(set.fetch(schema.name), set.fetch(schema.age))
            }
        }

        val listing = props.getOrThrow()
        listing.forEach {
            println("NameOf: ${it.first}||${it.second}")
        }
        assertTrue(listing.isNotEmpty())
    }
}