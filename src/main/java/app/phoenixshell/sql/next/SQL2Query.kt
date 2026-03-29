package app.phoenixshell.sql.next

import app.phoenixshell.sql.SQLFieldName

fun <T> insert(callback: (StatementBuilder<T>) -> StatementBuilder<T>): StatementBuilder<T> {
    val statementBuilder = StatementBuilder<T>()
    callback(statementBuilder)
    return statementBuilder
}

fun <T> query(callback: (StatementBuilder<T>) -> StatementBuilder<T>): StatementBuilder<T> {
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

    fun using(table: T): PreparedQuery<T> {
        val statement = callback!!.invoke(table)
        return PreparedQuery(table, statement, bindings)
    }
}

data class SQLBinding<T>(
    val sqlFieldName: SQLFieldName<T>,
    val value: T
)

class PreparedQuery<T>(
    val table: T,
    val statement: String,
    val bindings: List<SQLBinding<*>>
) {
    operator fun get(index: Int): SQLBinding<*> {
        return bindings[index]
    }
}