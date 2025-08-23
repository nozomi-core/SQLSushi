package app.phoenixshell.sql.sample2

import app.phoenixshell.sql.SQLSchema
import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.buildMapper
import app.phoenixshell.sql.buildQuery
import app.phoenixshell.sql.maps
import app.phoenixshell.sql.sample.app.Tables

data class ConversationModel(
    val message: String,
    val date: Int
)

object AppTable: SQLSchema() {
    object Conversation: SQLTable(this,"conversation") {
        val message = string("message")
        val date = int("date")
    }
}

val ConversationMap = buildMapper(AppTable.Conversation) {
    with(schema) {
        ConversationModel(
            message = get(message),
            date = get(date)
        )
    }
}

object ConversationQuery {

    fun insert(model: ConversationModel) = buildQuery<AppTable.Conversation> { opt, table, query, bind ->
        with(table) {
            query("""
                insert into $table($message, $date) values (${bind(message)}, ${bind(date)})
            """)
                .args(
                    message maps model.message,
                    date maps model.date
                )
        }
    }

    fun all() = buildQuery<AppTable.Conversation> { options, schema, statement, bind ->
        with(schema) {
            statement("""
                    select * from $table
                """).args()
        }
    }
}