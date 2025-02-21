package app.phoenixshell.sql

typealias QueryBuilderFactory<Schema> = (SQLSelection, Schema, TemplateBuilder) -> SQLTemplate<Schema>
typealias TemplateBuilder = (String) -> SQLTemplateBinding

class SQLTemplateBinding(private val templateSQL: String) {
    fun <Schema> bind(vararg bindings: Pair<SQLFieldName<*>, *>): SQLTemplate<Schema> {
        return SQLBindingTemplate(templateSQL, bindings.toList())
    }
}

interface SQLTemplate<Schema>

internal class SQLSyntaxTemplate<Schema>(internal val factory: QueryBuilderFactory<Schema>): SQLTemplate<Schema>
internal class SQLBindingTemplate<Schema>(
    internal val sqlTemplate: String,
    internal val bindings: List<Pair<SQLFieldName<*>, *>>
    ): SQLTemplate<Schema>

class SQLSelection(private val fields: Array<SQLFieldName<*>>) {
    override fun toString(): String = fields.joinToString(separator = ",")
}

open class SQLQueryList {
    fun <Schema> buildQuery(callback: QueryBuilderFactory<Schema>): SQLTemplate<Schema> {
        return SQLSyntaxTemplate(callback)
    }
}

infix fun <T> SQLFieldName<T>.binds(value: T): Pair<SQLFieldName<T>, T> = this to value
