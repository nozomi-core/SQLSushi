package app.phoenixshell.sql

import app.phoenixshell.sql.query.bindValue
import java.sql.PreparedStatement

fun <T> query(callback: (WhereBuilder<T>) -> WhereBuilder<T>): WhereBuilder<T> {
    val statementBuilder = WhereBuilder<T>()
    callback(statementBuilder)
    return statementBuilder
}

class WhereBuilder<T> {
    private val bindings = mutableListOf<SQLBinding<*>>()

    private var callback: (T.() -> String)? = null

    fun where(callback: T.() -> String): WhereBuilder<T> {
        this.callback = callback
        return this
    }

    fun whereAll(): WhereBuilder<T> {
        callback = { "where 1=1" }
        return this
    }

    operator fun <Q> invoke(field: SQLFieldName<Q>, value: Q): String {
        bindings.add(SQLBinding(field, value))
        return "?"
    }

    fun using(table: T): WhereQuery<T> {
        val statement = callback!!.invoke(table)
        return WhereQuery(table, statement, bindings)
    }
}

data class SQLBinding<T>(
    val sqlFieldName: SQLFieldName<T>,
    val value: T
)

class WhereQuery<T>(
    val table: T,
    val statement: String,
    val bindings: List<SQLBinding<*>>
) {
    operator fun get(index: Int): SQLBinding<*> {
        return bindings[index]
    }

    fun bind(startIndex: Int, stmt: PreparedStatement) {
        bindings.forEachIndexed { index, binding ->
            stmt.bindValue(index + startIndex, binding.value)
        }
    }
}