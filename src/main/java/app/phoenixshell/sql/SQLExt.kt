package app.phoenixshell.sql

import java.sql.ResultSet

//Result Set
fun ResultSet.fetch(name: SQLFieldName<Int>): Int {
    return getInt(name.field)
}

fun ResultSet.fetch(name: SQLFieldName<String>): String {
    return getString(name.field)
}

fun ResultSet.fetch(name: SQLFieldName<Boolean>): Boolean {
    return getBoolean(name.field)
}

fun ResultSet.fetch(name: SQLFieldName<Long>): Long {
    return getLong(name.field)
}

fun ResultSet.fetch(name: SQLFieldName<Float>): Float {
    return getFloat(name.field)
}

fun ResultSet.fetch(name: SQLFieldName<Double>): Double {
    return getDouble(name.field)
}

inline fun ResultSet.forEach(callback: ResultSet.() -> Unit) {
    while(next()) {
        callback(this)
    }
}

inline fun <Schema, Data> ResultSet.map(schema: Schema, mapper: SQLMapper<Schema, Data>): List<Data> {
    val list = mutableListOf<Data>()

    while(next()) {
        list.add(mapper(schema, this))
    }
    return list
}

