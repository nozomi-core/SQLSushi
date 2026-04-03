package app.phoenixshell.sql.query

import app.phoenixshell.sql.SQLContext
import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.WhereQuery
import kotlinx.serialization.*
import java.sql.PreparedStatement

inline fun <reified T> SQLContext.insert(
    table: SQLTable,
    value: T
) {
    val descriptor = serializer<T>().descriptor
    val columns = (0 until descriptor.elementsCount)
        .joinToString(", ") { descriptor.getElementName(it) }
    val placeholders = (0 until descriptor.elementsCount)
        .joinToString(", ") { "?" }

    prepare("INSERT INTO $table ($columns) VALUES ($placeholders)") { stmt ->
        val encoder = PreparedStatementEncoder(stmt)
        serializer<T>().serialize(encoder, value)
        stmt.executeUpdate()
    }
}

// Encode a data class into an UPDATE prepared statement
inline fun <reified T> SQLContext.update(
    query: WhereQuery<*>,
    value: T
) {
    val descriptor = serializer<T>().descriptor
    val setClause = (0 until descriptor.elementsCount)
        .joinToString(", ") { "${descriptor.getElementName(it)} = ?" }

    val fullStatement = "UPDATE ${query.table} SET $setClause ${query.statement.trim()}"

    prepare(fullStatement) { stmt ->
        // bind all fields first
        val encoder = PreparedStatementEncoder(stmt)
        serializer<T>().serialize(encoder, value)

        // then bind the where value after all fields
        val whereIndex = descriptor.elementsCount + 1

        query.bind(whereIndex, stmt)

        //stmt.bindValue(whereIndex, whereValue)
        stmt.executeUpdate()
    }
}
// Decode a SELECT result into a list of data classes
inline fun <reified T> SQLContext.select(
    query: WhereQuery<*>,
): List<T> {
    val fullStatement = "SELECT * FROM ${query.table} ${query.statement.trim()}"

    return prepare(fullStatement) { stmt ->
        query.bind(0, stmt)

        val rs = stmt.executeQuery()
        buildList {
            while (rs.next()) {
                val decoder = ResultSetDecoder(rs, serializer<T>().descriptor)
                add(serializer<T>().deserialize(decoder))
            }
        }
    }
}

fun SQLContext.delete(
    table: String,
    whereColumn: String,
    whereValue: Any?
) {
    prepare("DELETE FROM $table WHERE $whereColumn = ?") { stmt ->
        stmt.bindValue(1, whereValue)
        stmt.executeUpdate()
    }
}

fun PreparedStatement.bindValue(index: Int, value: Any?) {
    when (value) {
        null         -> setNull(index, java.sql.Types.NULL)
        is Int       -> setInt(index, value)
        is Long      -> setLong(index, value)
        is Float     -> setFloat(index, value)
        is Double    -> setDouble(index, value)
        is Boolean   -> setBoolean(index, value)
        is String    -> setString(index, value)
        else         -> setString(index, value.toString())
    }
}