package app.phoenixshell.sql

import java.sql.ResultSet

fun <Schema, Data> ResultSet.map(schema: Schema, func: SQLMapper<Schema, Data>, errorCallback: (Exception, ResultSet) -> Unit): List<Data> {
    val schemaMapper = SchemaMapper(schema, this, func, errorCallback)
    return schemaMapper.toList()
}