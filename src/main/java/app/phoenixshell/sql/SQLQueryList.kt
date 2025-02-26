package app.phoenixshell.sql

//TODO I will clean and reorganise this file later. The API binding works the way I wanted to implement

typealias QueryBuilderFactory<Schema> = (QueryOptions, Schema, Statement, Bind) -> SQLTemplate<Schema>
typealias Statement = (String) -> SQLTemplateBinding
typealias Bind = (SQLFieldName<*>) -> String

data class QueryOptions(
    val selection: Selection = Selection(arrayOf()),
    val limit: Int = 50
) {
    override fun toString(): String = ""
}

class BuildTemplate {
    internal val bindingPlaceHolders = mutableListOf<SQLFieldName<*>>()

    fun statement(sql: String): SQLTemplateBinding {
        return SQLTemplateBinding(sql, bindingPlaceHolders.toTypedArray())
    }

    fun binding(field: SQLFieldName<*>): String {
        bindingPlaceHolders.add(field)
        return "?"
    }
}

class SQLTemplateBinding(
    private val templateSQL: String,
    private val bindingPlaceHolders: Array<SQLFieldName<*>>
) {
    private val bindingMap = mutableMapOf<SQLFieldName<*>, Any?>()

    fun <Schema> args(vararg bindings: Pair<SQLFieldName<*>, *>): SQLTemplate<Schema> {
        bindings.forEach {
            bindingMap[it.first] = it.second
        }

        return SQLTemplate.Binding(templateSQL, bindingPlaceHolders, bindingMap)
    }
}

sealed class SQLTemplate<Schema> {
    internal class Syntax<Schema>(internal val factory: QueryBuilderFactory<Schema>): SQLTemplate<Schema>()

    internal class Binding<Schema>(
        internal val sqlTemplate: String,
        internal val bindingOrder: Array<SQLFieldName<*>>,
        internal val bindingValueMap: Map<SQLFieldName<*>, Any?>
    ): SQLTemplate<Schema>()
}

class Selection(private val fields: Array<SQLFieldName<*>>) {
    override fun toString(): String {
        return if(fields.isEmpty()) {
            "*"
        } else {
            fields.joinToString(separator = ",")
        }
    }
}

open class SQLQueryList {
    fun <Schema> buildQuery(callback: QueryBuilderFactory<Schema>): SQLTemplate<Schema> {
        return SQLTemplate.Syntax(callback)
    }
}

infix fun <T> SQLFieldName<T>.maps(value: T): Pair<SQLFieldName<T>, T> = this to value
