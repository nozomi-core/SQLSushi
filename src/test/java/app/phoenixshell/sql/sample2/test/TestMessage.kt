package app.phoenixshell.sql.sample2.test

import app.phoenixshell.sql.DatabaseMode
import app.phoenixshell.sql.DefaultSQLConnection
import app.phoenixshell.sql.DefaultSQLiteEngine
import app.phoenixshell.sql.buildMigrations
import app.phoenixshell.sql.createDatabase
import app.phoenixshell.sql.insert
import app.phoenixshell.sql.sample2.AppTable
import app.phoenixshell.sql.sample2.ConversationModel
import app.phoenixshell.sql.sample2.ConversationQuery
import org.junit.jupiter.api.Test

class TestMessage {

    @Test
    fun testMessage() {
        val db = createDatabase(
            targetVersion = 1,
            name = "test_message.db",
            mode = DatabaseMode.External,
            connection = DefaultSQLConnection,
            migrations = buildMigrations {
                version(1) { tact ->
                    with(AppTable.Conversation) {
                        tact.exec("create table $table($message text, $date integer)")
                    }
                }
            },
            engine = DefaultSQLiteEngine
        )

        val action = ConversationQuery.insert(ConversationModel("message", 123))

        db.useTransaction { tact ->
            tact.insert(action)
        }
    }

    @Test
    fun testMessage2() {
        val db = createDatabase(
            targetVersion = 1,
            name = "test_message.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLConnection,
            migrations = buildMigrations {
                version(1) { tact ->
                    with(AppTable.Conversation) {
                        tact.exec("create table $table($message text, $date integer)")
                    }
                }
            },
            engine = DefaultSQLiteEngine
        )

        val insert = ConversationQuery.insert(ConversationModel("message", 123))

    }
}