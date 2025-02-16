package app.phoenixshell.sql

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class SQLPreparedStatement(
    private val sql: String,
    private val preparedStatement: PreparedStatement,
    private val fields: Array<String>
) {
    private fun <T> indexSet(field: String, value: T, callback: (Int, T) -> Unit) {
        if(value != null) {
            val index = fields.indexOf(field) + 1
            callback(index, value)
        }
    }

    fun set(field: String, char: Char?) {
        indexSet(field, char) { index, value ->
            preparedStatement.setString(index, "$value")
        }
    }

    fun set(field: String, short: Short?) {
        indexSet(field, short) { index, value ->
            preparedStatement.setShort(index, value!!)
        }
    }

    fun set(field: String, int: Int?) {
        indexSet(field, int) { index, value ->
            preparedStatement.setInt(index, value!!)
        }
    }

    fun set(field: String, long: Long?) {
        indexSet(field, long) { index, value ->
            preparedStatement.setLong(index, value!!)
        }
    }

    fun set(field: String, float: Float?) {
        indexSet(field, float) { index, value ->
            preparedStatement.setFloat(index, value!!)
        }
    }

    fun set(field: String, double: Double?) {
        indexSet(field, double) { index, value ->
            preparedStatement.setDouble(index, value!!)
        }
    }

    fun set(field: String, string: String?) {
        indexSet(field, string) { index, value ->
            preparedStatement.setString(index, value!!)
        }
    }

    fun set(field: String, boolean: Boolean?) {
        indexSet(field, boolean) { index, value ->
            preparedStatement.setInt(index, if(value!!) 1 else 0)
        }
    }

    fun <T> executeInsert(idGen: SQLIdGenerator<T>, field: String, existingId: T?): T {
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

        fun create(conn: Connection, sql: String, fields: Array<String>): SQLPreparedStatement {
            val newSql = replaceAlphaNumVariables(sql, fields)
            return SQLPreparedStatement(newSql, conn.prepareStatement(newSql, Statement.RETURN_GENERATED_KEYS), fields)
        }
    }
}