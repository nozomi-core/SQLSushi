package app.phoenixshell.sql

import java.sql.ResultSet

typealias SQLMapper<Schema, Data> = SchemaMapper<Schema, Data>.() -> Data

class SchemaMapper<Schema, Data>(
    val schema: Schema,
    private val resultSet: ResultSet,
    private val mapper: SQLMapper<Schema, Data>,
    private val errorCallback: (Exception, ResultSet) -> Unit
) {
    fun get(name: SQLFieldName<Int>): Int {
        return resultSet.getInt(name.field)
    }

    fun get(name: SQLFieldName<String>): String {
        return resultSet.getString(name.field)
    }

    fun get(name: SQLFieldName<Boolean>): Boolean {
        return resultSet.getBoolean(name.field)
    }

    fun get(name: SQLFieldName<Long>): Long {
        return resultSet.getLong(name.field)
    }

    fun get(name: SQLFieldName<Float>): Float {
        return resultSet.getFloat(name.field)
    }

    fun get(name: SQLFieldName<Double>): Double {
        return resultSet.getDouble(name .field)
    }

    fun toList(): List<Data> {
        val list = mutableListOf<Data>()

        while(resultSet.next()) {
            try {
                list.add(mapper(this))
            } catch (e: Exception) {
                errorCallback(e, resultSet)
            }
        }

        return list
    }
}