package app.phoenixshell.sql.builder

import app.phoenixshell.sql.SQLFieldName

internal enum class WhereCondition {
    EQUALS,
    GREATER
}

internal interface WhereClause {

}

internal interface QueryBuilder {
    fun <T> where(field: SQLFieldName<T>, operation: WhereCondition, value: T)
    fun select(vararg fields: SQLFieldName<*>)
}

internal interface SQLBuilder {

    fun query(callback: QueryBuilder.() -> Unit)
}