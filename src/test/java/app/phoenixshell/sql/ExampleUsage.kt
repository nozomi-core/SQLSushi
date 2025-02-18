package app.phoenixshell.sql

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

object Queries {
    object User: SQLQueryList() {
        val selectAll = with(Schema.Users) {
            query("select * from $tableName")
        }

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

        fun longQuery(age: String) = buildQuery<LocationData> { selection, schema, template ->
            template(
                """
                    select $selection from ${schema.tableName} where ${schema.position()} = ? 
                """
            ).bind {
                set(schema.position(), age)
            }
        }
    }
}

interface DerivedSchema {
    val tableName: String
}

interface LocationData: DerivedSchema {
    fun position(): SQLFieldName<String>
}

object Schema {
    object Users: SQLTableName("users"), LocationData {
        val age = int("age")
        val isValid = boolean("is_valid")
        val location = string("position")

        override fun position(): SQLFieldName<String> = location
    }
}

data class SomeData(val age: Int, val isValid: Boolean)

val MyMapper: SQLMapper<SomeData> = {
    with(Schema.Users) {
        SomeData(
            age = unwrap(age),
            isValid = unwrap(isValid)
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
            val example = tact.prepare("select * from ${Schema.Users.tableName}", arrayOf()).executeQuery().map(MyMapper)
            example.forEach {
                println("MyAge: ${it.age}")
            }
        }
    }
}