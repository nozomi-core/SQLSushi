package app.phoenixshell.sql.query

import app.phoenixshell.sql.SQLContext
import app.phoenixshell.sql.SQLReadContext
import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.WhereBuilder
import app.phoenixshell.sql.WhereQuery
import kotlinx.serialization.*
import java.sql.PreparedStatement

inline fun <reified S: SQLTable, reified T> S.insert(
    context: SQLContext,
    value: T
) {
    if(value is Iterable<*>) {
        throw Exception("Value must not be an iterable")
    }

    val descriptor = serializer<T>().descriptor
    val columns = (0 until descriptor.elementsCount)
        .joinToString(", ") { descriptor.getElementName(it) }
    val placeholders = (0 until descriptor.elementsCount)
        .joinToString(", ") { "?" }

    context.prepare("INSERT INTO $table ($columns) VALUES ($placeholders)") { stmt ->
        val encoder = PreparedStatementEncoder(stmt)
        serializer<T>().serialize(encoder, value)
        stmt.executeUpdate()
    }
}

inline fun <reified S: SQLTable, reified T> S.insertAll(context: SQLContext, values: Iterable<T>) {
    values.forEach {
        insert(context, it)
    }
}

inline fun <reified S: SQLTable, reified T> S.update(
    context: SQLContext,
    builder: WhereBuilder<S>,
    value: T
) {
    val query = builder.using(this)

    val descriptor = serializer<T>().descriptor
    val setClause = (0 until descriptor.elementsCount)
        .joinToString(", ") { "${descriptor.getElementName(it)} = ?" }

    val fullStatement = "UPDATE ${query.table} SET $setClause ${query.statement.trim()}"

    context.prepare(fullStatement) { stmt ->
        val encoder = PreparedStatementEncoder(stmt)
        serializer<T>().serialize(encoder, value)

        val whereIndex = descriptor.elementsCount + 1
        query.bind(whereIndex, stmt)
        stmt.executeUpdate()
    }
}
// Decode a SELECT result into a list of data classes
inline fun <reified T> WhereQuery<*>.asList(context: SQLReadContext): List<T> {
    val fullStatement = "SELECT * FROM ${this.table} ${this.statement.trim()}"

    return context.prepare(fullStatement) { stmt ->
        bind(1, stmt)

        val rs = stmt.executeQuery()
        buildList {
            while (rs.next()) {
                val decoder = ResultSetDecoder(rs, serializer<T>().descriptor)
                add(serializer<T>().deserialize(decoder))
            }
        }
    }
}

fun <S: SQLTable> S.delete(
    context: SQLContext,
    builder: WhereBuilder<S>
) {
    val where = builder.using(this)

    if(where.bindings.isEmpty()) {
        throw Exception("Cannot call delete query without a where clause")
    }

    context.prepare("DELETE FROM ${where.table} ${where.statement}") { stmt ->
        where.bind(1, stmt)

        stmt.executeUpdate()
    }
}

enum class SortOrder { ASC, DESC }

fun PreparedStatement.bindValue(index: Int, value: Any?) {
    when (value) {
        null         -> setNull(index, java.sql.Types.NULL)
        is Int       -> setInt(index, value)
        is Long      -> setLong(index, value)
        is Float     -> setFloat(index, value)
        is Double    -> setDouble(index, value)
        is Boolean   -> setBoolean(index, value)
        is String    -> setString(index, value)
        else         -> throw IllegalArgumentException("Unsupported type ${value.javaClass}")
    }
}