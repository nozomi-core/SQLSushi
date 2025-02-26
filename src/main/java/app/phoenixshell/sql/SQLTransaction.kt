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
){
    private var isOpen = true

    fun prepare(sql: String, fields: Array<SQLFieldName<*>>): SQLPreparedStatement {
        return runWithTransaction {
            SQLPreparedStatement.create(connection, sql, fields)
        }
    }

    fun exec(sql: String) {
        runWithTransaction {
            connection.createStatement().execute(sql)
        }
    }

    fun <Schema> query(context: Schema, query: SQLTemplate<Schema>): ResultMapping<Schema> {
        return runWithTransaction {
            val results = prepareStatement(context, query).executeQuery()
            ResultMapping(context, results)
        }
    }

    fun <Schema> insert(context: Schema, query: SQLTemplate<Schema>) {
        runWithTransaction {
            prepareStatement(context, query).executeUpdate()
        }
    }

    private val templateBuilder: TemplateBuilder = {
        SQLTemplateBinding(it)
    }

    private fun <Schema> prepareStatement(context: Schema, query: SQLTemplate<Schema>, selection: (Schema) -> Array<SQLFieldName<*>> = { arrayOf() }): SQLPreparedStatement {
        val factory = query as SQLTemplate.Syntax<Schema>
        val projection = selection(context)

        val bindingTemplate = factory.factory(Selection(projection), context, templateBuilder) as SQLTemplate.Binding<Schema>

        val fieldList = bindingTemplate.bindings.map {
            it.first
        }.toTypedArray()

        val prepStatement = prepare(bindingTemplate.sqlTemplate, fieldList)
        bindingTemplate.bindings.forEach { pair ->
            prepStatement.setAny(pair.first, pair.second)
        }

        return prepStatement
    }

    private fun <T> runWithTransaction(callback: () -> T): T {
        return if(!isOpen) {
            throw Exception("This transaction is closed")
        } else callback()
    }

    internal fun close() {
        isOpen = false
    }
}