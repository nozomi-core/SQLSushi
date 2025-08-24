package app.phoenixshell.sql

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import java.sql.Connection
import java.sql.ResultSet

val JsonDecoder = Json {
    ignoreUnknownKeys = true
}

class ResultMapping<Schema>(
    val schema: Schema,
    val results: ResultSet
) {
    fun <R> map(mapper: SQLMapper<Schema, R>): List<R> {
        return results.map(schema, mapper) { e, result ->
            e.printStackTrace()
        }
    }

    @OptIn(InternalSerializationApi::class)
    inline fun <reified T> decodeSingle(): T {
        val columnCount = results.metaData.columnCount
        val jsonMap = mutableMapOf<String, JsonElement>()


        for (i in 1..columnCount) {
            val columnName = results.metaData.getColumnLabel(i)
            val value = results.getObject(i)

            jsonMap[columnName] = when (value) {
                null -> JsonNull
                is Int -> JsonPrimitive(value)
                is Long -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is Double -> JsonPrimitive(value)
                is Float -> JsonPrimitive(value)
                is String -> JsonPrimitive(value)
                else -> JsonPrimitive(value.toString()) // or handle custom types
            }
        }

        val json = JsonObject(jsonMap)
        return JsonDecoder.decodeFromJsonElement(serializer(), json)
    }
}

class SQLTransaction internal constructor(
    private val connection: Connection
){
    private var isOpen = true

    fun prepare(sql: String, fields: Array<SQLFieldName<*>>): SQLPreparedStatement {
        return runWithTransaction {
            SQLPreparedStatement.create(connection, sql, fields)
        }
    }

    fun exec(sql: String) {
        runWithTransaction {
            connection.createStatement().execute(sql)
        }
    }

    fun <Schema> query(schema: Schema, query: SQLTemplate<Schema>, options: QueryOptions = QueryOptions(), selection: (Schema) -> Array<SQLFieldName<*>> = { arrayOf()}): ResultMapping<Schema> {
        return runWithTransaction {
            val queryProjection = selection(schema)

            val results = prepareStatement(schema, query, options.copy(selection = Selection(queryProjection))).executeQuery()
            ResultMapping(schema, results)
        }
    }

    fun <Schema> insert(query: SQLQuery<Schema>) {
        return insert(query.table, query.template)
    }

    fun <Schema> insert(context: Schema, query: SQLTemplate<Schema>) {
        runWithTransaction {
            prepareStatement(context, query, QueryOptions()).executeUpdate()
        }
    }

    private fun <Schema> prepareStatement(context: Schema, query: SQLTemplate<Schema>, options: QueryOptions): SQLPreparedStatement {
        val factory = query as SQLTemplate.Syntax<Schema>

        val buildTemplate = BuildTemplate()
        val bindingTemplate = factory.factory(options, context, buildTemplate::statement, buildTemplate::binding) as SQLTemplate.Binding<Schema>


        val prepStatement = prepare(bindingTemplate.sqlTemplate, bindingTemplate.bindingOrder)

        bindingTemplate.bindingOrder.forEach {
            prepStatement.setAny(it, bindingTemplate.bindingValueMap[it])
        }

        return prepStatement
    }

    private fun <T> runWithTransaction(callback: () -> T): T {
        return if(!isOpen) {
            throw Exception("This transaction is closed")
        } else callback()
    }

    internal fun close() {
        isOpen = false
    }
}