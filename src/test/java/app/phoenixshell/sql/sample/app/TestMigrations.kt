package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLDatabaseMigration
import app.phoenixshell.sql.SQLDatabaseMigrationFactory
import app.phoenixshell.sql.SQLTransaction

object TestMigration001: SQLDatabaseMigration {
    override val version: Int = 1

    override fun onMigrate(tact: SQLTransaction) {
        TestSchema.User.run {
            tact.exec("""
                create table $tableName($nameField text, $birthYearField integer);
            """.trimIndent())
        }
    }

    override fun onPostMigrate(tact: SQLTransaction) {}
}

object TestMigrations: SQLDatabaseMigrationFactory {
    override fun onCreateMigrations(): Array<SQLDatabaseMigration> {
        return arrayOf(
            TestMigration001
        )
    }
}