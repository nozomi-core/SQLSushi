package app.phoenixshell.sql

import app.phoenixshell.sql.query.bindValue
import java.sql.PreparedStatement

fun <T> where(callback: (WhereBuilder<T>) -> String): WhereBuilder<T> {
    val statementBuilder = WhereBuilder<T>()
    return statementBuilder.where {
        callback(statementBuilder)
    }
}

fun <T> whereAll(): WhereBuilder<T> {
    val statementBuilder = WhereBuilder<T>()
    return statementBuilder.whereAll()
}

class WhereBuilder<T> {
    private val bindings = mutableListOf<SQLBinding<*>>()

    private var callback: (T.() -> String)? = null

    internal fun where(callback: T.() -> String): WhereBuilder<T> {
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