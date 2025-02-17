package app.phoenixshell.sql

import java.sql.ResultSet

inline fun <reified T> ResultSet.get(name: SQLFieldName): T {
    return when(T::class.java) {
        String::class.java -> getString(name.field)
        Int::class.java -> getInt(name.field)
        Long::class.java -> getLong(name.field)
        Float::class.java -> getFloat(name.field)
        Double::class.java -> getDouble(name.field)

        else -> {throw Exception("${T::class.java.canonicalName} is not supported type")}
    } as T

}