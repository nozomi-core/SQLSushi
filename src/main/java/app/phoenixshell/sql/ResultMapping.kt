package app.phoenixshell.sql

import java.sql.ResultSet

class ResultMapping<Schema>(
    val schema: Schema,
    val results: ResultSet,
    val resultDecoder: ResultDecoder
) {
    fun <R> map(mapper: SQLMapper<Schema, R>): List<R> {
        return results.map(schema, mapper) { e, result ->
            e.printStackTrace()
        }
    }

    inline fun <reified T> decodeSingle(): T {
        return resultDecoder.decode(T::class.java, results)
    }

    inline fun <reified T> decodeList(): List<T> {
        return buildList {
            while (results.next()) {
                add(decodeSingle())
            }
        }
    }
}