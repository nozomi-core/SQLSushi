package app.phoenixshell.sql

import java.sql.Connection

class SQLTransaction internal constructor(
    private val connection: Connection,
    private val resultDecoder: ResultDecoder,
){
    private var isOpen = true

    internal fun prepare(sql: String, fields: Array<SQLFieldName<*>>): SQLPreparedStatement {
        return runWithTransaction {
            SQLPreparedStatement.create(connection, sql, fields)
        }
    }

    fun exec(sql: String) {
        runWithTransaction {
            connection.createStatement().execute(sql)
        }
    }

    internal fun <Schema> query(schema: Schema, query: SQLTemplate<Schema>, options: QueryOptions = QueryOptions(), selection: (Schema) -> Array<SQLFieldName<*>> = { arrayOf()}): ResultMapping<Schema> {
        return runWithTransaction {
            val queryProjection = selection(schema)

            val results = prepareStatement(schema, query, options.copy(selection = Selection(queryProjection))).executeQuery()
            ResultMapping(schema, results, resultDecoder)
        }
    }

    fun <Schema> query(query: SQLQuery<Schema>, options: QueryOptions = QueryOptions(), selection: (Schema) -> Array<SQLFieldName<*>> = { arrayOf()}): ResultMapping<Schema> {
        return query(query.table, query.template, options, selection)
    }

    fun <Schema> insert(query: SQLQuery<Schema>) {
        return insert(query.table, query.template)
    }

    internal fun <Schema> insert(context: Schema, query: SQLTemplate<Schema>) {
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