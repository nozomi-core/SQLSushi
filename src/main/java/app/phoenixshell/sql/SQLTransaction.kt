package app.phoenixshell.sql

import java.sql.Connection
import java.sql.ResultSet

class ResultMapping<Schema>(
    private val schema: Schema,
    private val results: ResultSet
) {

    fun <R> map(mapper: SQLMapper<Schema, R>): List<R> {
        return results.map(schema, mapper)
    }
}

class SQLTransaction internal constructor(
    private val connection: Connection
) {
    fun prepare(sql: String, fields: Array<SQLFieldName<*>>): SQLPreparedStatement {
        return SQLPreparedStatement.create(connection, sql, fields)
    }

    fun exec(sql: String) {
        connection.createStatement().execute(sql)
    }

    private val templateBuilder: TemplateBuilder = {
        SQLTemplateBinding(it)
    }

    fun <Schema> query(context: Schema, query: SQLTemplate<Schema>, selection: (Schema) -> Array<SQLFieldName<*>>): ResultMapping<Schema> {
        val factory = query as SQLSyntaxTemplate<Schema>
        val projection = selection(context)

        val bindingTemplate = factory.factory(SQLSelection(projection), context, templateBuilder) as SQLBindingTemplate<Schema>

        val fieldList = bindingTemplate.bindings.map {
            it.first
        }.toTypedArray()

        val prepStatement = prepare(bindingTemplate.sqlTemplate, fieldList)
        bindingTemplate.bindings.forEach { pair ->
            prepStatement.setAny(pair.first, pair.second)
        }

        val results = prepStatement.executeQuery()
        return ResultMapping(context, results)
    }
}