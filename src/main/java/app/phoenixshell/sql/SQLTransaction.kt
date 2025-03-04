package app.phoenixshell.sql

import java.sql.Connection
import java.sql.ResultSet

class ResultMapping<Schema>(
    private val schema: Schema,
    private val results: ResultSet
) {
    fun <R> map(mapper: SQLMapper<Schema, R>): List<R> {
        return results.map(schema, mapper) { e, result ->
            e.printStackTrace()
        }
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

    fun <Schema> query(context: Schema, query: SQLTemplate<Schema>, options: QueryOptions = QueryOptions(), selection: (Schema) -> Array<SQLFieldName<*>> = { arrayOf()}): ResultMapping<Schema> {
        return runWithTransaction {
            val queryProjection = selection(context)

            val results = prepareStatement(context, query, options.copy(selection = Selection(queryProjection))).executeQuery()
            ResultMapping(context, results)
        }
    }

    fun <Schema> insert(context: Schema, query: SQLTemplate<Schema>) {
        runWithTransaction {
            prepareStatement(context, query, QueryOptions()).executeUpdate()
        }
    }

    private fun <Schema> prepareStatement(context: Schema, query: SQLTemplate<Schema>, options: QueryOptions): SQLPreparedStatement {
        val factory = query as SQLTemplate.Syntax<Schema>

        val buildTemplate = BuildTemplate()
        val bindingTemplate = factory.factory(options, context, buildTemplate::statement, buildTemplate::binding) as SQLTemplate.Binding<Schema>


        val prepStatement = prepare(bindingTemplate.sqlTemplate, bindingTemplate.bindingOrder)

        bindingTemplate.bindingOrder.forEach {
            prepStatement.setAny(it, bindingTemplate.bindingValueMap[it])
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