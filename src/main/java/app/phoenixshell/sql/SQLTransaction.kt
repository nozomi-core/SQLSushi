package app.phoenixshell.sql

import java.sql.Connection
import java.sql.ResultSet

class ResultMapping<Schema>(
    private val schema: Schema,
    private val results: ResultSet
) {

    fun <R> map(mapper: SQLMapper<Schema, R>): List<R> {
        return listOf()
    }
}

typealias SelectionFields<Schema> =  Schema.() -> List<SQLFieldName<*>>

class Selection<Schema>(val select: SelectionFields<Schema>)

class SQLTransaction internal constructor(
    private val connection: Connection
) {
    fun prepare(sql: String, fields: Array<SQLFieldName<*>>): SQLPreparedStatement {
        return SQLPreparedStatement.create(connection, sql, fields)
    }

    fun exec(sql: String) {
        connection.createStatement().execute(sql)
    }

    fun <Schema> perform(context: Schema, query: List<SQLTemplate<Schema>>, selection: (Schema) -> Array<SQLFieldName<*>>): ResultMapping<Schema> {
        return ResultMapping(context,  TODO())
    }
}