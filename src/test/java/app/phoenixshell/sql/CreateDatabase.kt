package app.phoenixshell.sql

import org.junit.jupiter.api.Test

class CreateDatabase {

    object ExampleMigration : SQLDatabaseMigration {
        override val version = 1

        override fun onMigrate(tact: SQLTransaction) {
            tact.exec("create table user(age integer);")
        }
    }
}