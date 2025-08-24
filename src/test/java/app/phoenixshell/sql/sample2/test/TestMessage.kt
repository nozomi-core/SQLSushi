package app.phoenixshell.sql.sample2.test

import app.phoenixshell.sql.DatabaseMode
import app.phoenixshell.sql.DefaultSQLiteConnection
import app.phoenixshell.sql.DefaultSQLiteEngine
import app.phoenixshell.sql.ResultDecoder
import app.phoenixshell.sql.ResultDecoderNotImplemented
import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.SQLTransaction
import app.phoenixshell.sql.buildMigrations
import app.phoenixshell.sql.createDatabase
import app.phoenixshell.sql.on
import app.phoenixshell.sql.sample2.AppTable
import app.phoenixshell.sql.sample2.ConversationModel
import app.phoenixshell.sql.sample2.ConversationQuery
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.sql.ResultSet

val conversationDecoder = object : ResultDecoder {
    override fun <T> decode(kClass: Class<T>, resultSet: ResultSet): T {
        return ConversationModel(
            message = resultSet.getString(AppTable.Conversation.message.field),
            date = resultSet.getInt(AppTable.Conversation.date.field)
        ) as T
    }

}

class TestMessage {

    @Test
    fun testMessage() {
        val db = createDatabase(
            targetVersion = 1,
            name = "test_message.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = buildMigrations {
                version(1) {

                    AppTable.Conversation.on(it) {
                        "create table $table($message text, $date integer)"
                    }

                }
            },
            engine = DefaultSQLiteEngine,
            resultDecoder = ResultDecoderNotImplemented
        )

        val action = ConversationQuery.insert(ConversationModel("message123", 123))

        db.useTransaction { tact ->
            tact.insert(action)
        }
    }

    @Test
    fun testMessage2() {
        val db = createDatabase(
            targetVersion = 1,
            name = "map_data.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = buildMigrations {
                version(1) { tact ->
                    with(AppTable.Conversation) {
                        tact.exec("create table $table($message text, $date integer)")
                    }
                }
            },
            engine = DefaultSQLiteEngine,
            resultDecoder = conversationDecoder
        )



        db.useTransaction { tact ->
            tact.insert(ConversationQuery.insert(ConversationModel("message123", 123)))
        }

        val single = db.useTransaction { tact ->
            val result = tact.query(ConversationQuery.all())
            result.decodeSingle<ConversationModel>()
        }

        assertEquals("message123", single.message)
    }

    @Test
    fun decodeList() {
        val db = createDatabase(
            targetVersion = 1,
            name = "decodelist.db",
            mode = DatabaseMode.Memory,
            connection = DefaultSQLiteConnection,
            migrations = buildMigrations {
                version(1) { tact ->
                    with(AppTable.Conversation) {
                        tact.exec("create table $table($message text, $date integer)")
                    }
                }
            },
            engine = DefaultSQLiteEngine,
            resultDecoder = conversationDecoder
        )

        db.useTransaction { tact ->
            tact.insert(ConversationQuery.insert(ConversationModel("first", 123)))
            tact.insert(ConversationQuery.insert(ConversationModel("next", 123)))
        }

        val list = db.useTransaction { tact ->
            val result = tact.query(ConversationQuery.all())
            result.decodeList<ConversationModel>()
        }

        assertEquals(2, list.size)
        assertEquals("first", list[0].message)
        assertEquals("next", list[1].message)
    }
}