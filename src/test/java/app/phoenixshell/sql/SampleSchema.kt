package app.phoenixshell.sql

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserModel(
    val id: String,
    val name: String,
    val createdAt: Long
)

object Tables: SQLSchema() {
    object User: SQLTable("users"), CreatedAt {
        val id = string("id")
        val name = string("name")
        override val createdAt = long("createdAt")
    }
}

object SampleMigration001: SQLDatabaseMigration {
    override val version: Int = 1

    override fun onMigrate(context: SQLMigrationContext) {
        Tables.User.run {
            context.exec(
                """
                create table $table($id text, $name text, $createdAt integer); 
                """.trimIndent()
            )
        }

        context.exec("create table popcorn(name text, createdAt integer);")
    }
}

object SampleMigration: SQLDatabaseMigrationFactory {
    override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
        return arrayOf(
            SampleMigration001
        )
    }
}

interface CreatedAt {
    val createdAt: SQLFieldName<Long>
}

fun Tables.User.whereCreatedAt(xCreatedAt: Long) = where<Tables.User> { bind ->
    """where $createdAt = ${bind(createdAt, xCreatedAt)}"""
}

fun getAll() = whereAll<Tables.User>()
fun Tables.User.getByName(xName: String) = where<Tables.User> { bind ->
    """where $name = ${bind(name, xName)}"""
}

fun createSampleDatabase(tag: String = ""): SQLDatabase {
    return createDatabase(
        targetVersion = 1,
        name = "db_${tag}_${UUID.randomUUID()}.db",
        mode = DatabaseMode.Memory,
        connection = DefaultSQLiteConnection,
        migrations = SampleMigration,
        engine = DefaultSQLiteEngine
    )
}