package app.phoenixshell.sql

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class SQLPreparedStatement private constructor(
    private val sql: String,
    private val preparedStatement: PreparedStatement,
    private val fields: Array<SQLFieldName<*>>
) {
    init {
        ensureWhereClause()
    }

    //TODO: Improve this, just a fast solution to stop statements with update and delete from being run without a where, just a band-aid solution
    private fun ensureWhereClause() {
        if("update" in sql || "delete" in sql) {

            if("where" !in sql) {
                throw Exception("SQLStatements for UPDATE or DELETE must contain a WHERE clause")
            }
        }
    }

    private inline fun <T> indexSet(field: SQLFieldName<*>, value: T, callback: (Int, T) -> Unit) {
        if(value != null) {
            val searchIndex = fields.indexOf(field)
            if(searchIndex == -1) {
                throw Exception("Not a valid field")
            }
            val index = searchIndex + 1
            callback(index, value)
        }
    }

    fun setAny(field: SQLFieldName<*>, value: Any?) {
        when(value) {
            is Char -> set(field as SQLFieldName<Char>, value)
            is Short -> set(field as SQLFieldName<Short>, value)
            is Int -> set(field as SQLFieldName<Int>, value)
            is Long -> set(field as SQLFieldName<Long>, value)
            is Float -> set(field as SQLFieldName<Float>, value)
            is Double -> set(field as SQLFieldName<Double>, value)
            is String -> set(field as SQLFieldName<String>, value)
            is Boolean -> set(field as SQLFieldName<Boolean>, value)

            else -> throw Exception("Type is not supported")
        }
    }

    fun set(field: SQLFieldName<Char>, char: Char?) {
        indexSet(field, char) { index, value ->
            preparedStatement.setString(index, "$value")
        }
    }

    fun set(field: SQLFieldName<Short>, short: Short?) {
        indexSet(field, short) { index, value ->
            preparedStatement.setShort(index, value!!)
        }
    }

    fun set(field: SQLFieldName<Int>, int: Int?) {
        indexSet(field, int) { index, value ->
            preparedStatement.setInt(index, value!!)
        }
    }

    fun set(field: SQLFieldName<Long>, long: Long?) {
        indexSet(field, long) { index, value ->
            preparedStatement.setLong(index, value!!)
        }
    }

    fun set(field: SQLFieldName<Float>, float: Float?) {
        indexSet(field, float) { index, value ->
            preparedStatement.setFloat(index, value!!)
        }
    }

    fun set(field: SQLFieldName<Double>, double: Double?) {
        indexSet(field, double) { index, value ->
            preparedStatement.setDouble(index, value!!)
        }
    }

    fun set(field: SQLFieldName<String>, string: String?) {
        indexSet(field, string) { index, value ->
            preparedStatement.setString(index, value!!)
        }
    }

    fun set(field: SQLFieldName<Boolean>, boolean: Boolean?) {
        indexSet(field, boolean) { index, value ->
            preparedStatement.setBoolean(index, value!!)
        }
    }

    fun execute(): Boolean {
        return preparedStatement.execute()
    }

    fun executeQuery(): ResultSet {
        return preparedStatement.executeQuery()
    }

    fun executeUpdate(): Int {
        return preparedStatement.executeUpdate()
    }

    override fun toString(): String {
        return sql
    }

    companion object {

        fun create(conn: Connection, sql: String, fields: Array<SQLFieldName<*>>): SQLPreparedStatement {
            return SQLPreparedStatement(sql.lowercase(), conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), fields)
        }
    }
}