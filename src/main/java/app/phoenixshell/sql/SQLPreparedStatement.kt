package app.phoenixshell.sql

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class SQLPreparedStatement(
    private val sql: String,
    private val preparedStatement: PreparedStatement,
    private val fields: Array<SQLFieldName>
) {
    private fun <T> indexSet(field: SQLFieldName, value: T, callback: (Int, T) -> Unit) {
        if(value != null) {
            val searchIndex = fields.indexOf(field)
            if(searchIndex == -1) {
                throw Exception("Not a valid field")
            }
            val index = searchIndex + 1
            callback(index, value)
        }
    }

    fun set(field: SQLFieldName, char: Char?) {
        indexSet(field, char) { index, value ->
            preparedStatement.setString(index, "$value")
        }
    }

    fun set(field: SQLFieldName, short: Short?) {
        indexSet(field, short) { index, value ->
            preparedStatement.setShort(index, value!!)
        }
    }

    fun set(field: SQLFieldName, int: Int?) {
        indexSet(field, int) { index, value ->
            preparedStatement.setInt(index, value!!)
        }
    }

    fun set(field: SQLFieldName, long: Long?) {
        indexSet(field, long) { index, value ->
            preparedStatement.setLong(index, value!!)
        }
    }

    fun set(field: SQLFieldName, float: Float?) {
        indexSet(field, float) { index, value ->
            preparedStatement.setFloat(index, value!!)
        }
    }

    fun set(field: SQLFieldName, double: Double?) {
        indexSet(field, double) { index, value ->
            preparedStatement.setDouble(index, value!!)
        }
    }

    fun set(field: SQLFieldName, string: String?) {
        indexSet(field, string) { index, value ->
            preparedStatement.setString(index, value!!)
        }
    }

    fun set(field: SQLFieldName, boolean: Boolean?) {
        indexSet(field, boolean) { index, value ->
            preparedStatement.setInt(index, if(value!!) 1 else 0)
        }
    }

    fun <T> executeInsert(idGen: SQLIdGenerator<T>, field: SQLFieldName, existingId: T?): T {
        return if(existingId == null) {
            val newId = idGen.generateId()
            set(field, newId.toString())
            newId
        } else {
            set(field, existingId.toString())
            existingId
        }.also {
            preparedStatement.execute()
        }
    }

    fun execute() {
        preparedStatement.execute()
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

        fun create(conn: Connection, sql: String, fields: Array<SQLFieldName>): SQLPreparedStatement {
            val newSql = replaceAlphaNumVariables(sql, fields)
            return SQLPreparedStatement(newSql, conn.prepareStatement(newSql, Statement.RETURN_GENERATED_KEYS), fields)
        }
    }
}