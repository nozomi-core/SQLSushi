package app.phoenixshell.sql

typealias QueryBuilderFactory<Schema> = (SQLSelection, Schema, TemplateBuilder) -> SQLTemplate<Schema>
typealias TemplateBuilder = (String) -> SQLTemplateBinding

typealias StatementBinding = SQLPreparedStatement.() -> Unit

class SQLTemplateBinding(private val templateSQL: String) {
    fun <Schema> bind(vararg fields: SQLFieldName<*>, callback: StatementBinding): SQLTemplate<Schema> {
        return SQLBindingTemplate(callback, templateSQL, fields.toList().toTypedArray())
    }
}

interface SQLTemplate<Schema>

internal class SQLSyntaxTemplate<Schema>(internal val factory: QueryBuilderFactory<Schema>): SQLTemplate<Schema>
internal class SQLBindingTemplate<Schema>(
    internal val binding: StatementBinding,
    internal val sqlTemplate: String,
    internal val format: Array<SQLFieldName<*>>
    ): SQLTemplate<Schema>

class SQLSelection(private val fields: Array<SQLFieldName<*>>) {
    override fun toString(): String = fields.joinToString(separator = ",")
}

open class SQLQueryList {
    fun <Schema> buildQuery(callback: QueryBuilderFactory<Schema>): SQLTemplate<Schema> {
        return SQLSyntaxTemplate(callback)
    }
}