package app.phoenixshell.sql

import java.sql.PreparedStatement
import java.sql.ResultSet

//Result Set
fun ResultSet.unwrap(name: SQLFieldName<Int>): Int {
    return getInt(name.field)
}

fun ResultSet.unwrap(name: SQLFieldName<String>): String {
    return getString(name.field)
}

fun ResultSet.unwrap(name: SQLFieldName<Boolean>): Boolean {
    return getBoolean(name.field)
}

fun ResultSet.unwrap(name: SQLFieldName<Long>): Long {
    return getLong(name.field)
}

fun ResultSet.unwrap(name: SQLFieldName<Float>): Float {
    return getFloat(name.field)
}

fun ResultSet.unwrap(name: SQLFieldName<Double>): Double {
    return getDouble(name.field)
}

//PreparedStatement
fun PreparedStatement.set(name: SQLFieldName<Int>, value: Int) {

}

fun PreparedStatement.set(name: SQLFieldName<String>, value: String) {

}

fun PreparedStatement.set(name: SQLFieldName<Boolean>, value: Boolean) {

}


inline fun ResultSet.forEach(callback: ResultSet.() -> Unit) {
    while(next()) {
        callback(this)
    }
}

inline fun <Schema, Data> ResultSet.from(schema: Schema, mapper: SQLMapper<Schema, Data>): List<Data> {
    val list = mutableListOf<Data>()

    while(next()) {
        list.add(mapper(schema, this))
    }
    return list
}

