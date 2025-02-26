package app.phoenixshell.sql

import java.sql.ResultSet

fun <Schema, Data> ResultSet.map(schema: Schema, func: SQLMapper<Schema, Data>): List<Data> {
    val schemaMapper = SchemaMapper(schema, this, func)
    return schemaMapper.toList()
}