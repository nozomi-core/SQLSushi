package app.phoenixshell.sql.next

import app.phoenixshell.sql.SQLFieldName
import java.sql.PreparedStatement

fun <T> insert(callback: (StatementBuilder<T>) -> StatementBuilder<T>): StatementBuilder<T> {
    val statementBuilder = StatementBuilder<T>()
    callback(statementBuilder)
    return statementBuilder
}

fun <T> where(callback: (StatementBuilder<T>) -> StatementBuilder<T>): StatementBuilder<T> {
    val statementBuilder = StatementBuilder<T>()
    callback(statementBuilder)
    return statementBuilder
}

class StatementBuilder<T> {
    private val bindings = mutableListOf<SQLBinding<*>>()

    private var callback: (T.() -> String)? = null

    fun prepare(callback: T.() -> String): StatementBuilder<T> {
        this.callback = callback
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
            stmt.setObject(index + startIndex, binding.value)
        }
    }
}