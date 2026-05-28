package app.phoenixshell.sql

import kotlin.random.Random

fun generateRandomInsert(table: SQLTable): String {
    val columns = table.columns
    val columnNames = columns.joinToString(", ") { it.field }
    val values = columns.joinToString(", ") { randomValueFor(it) }

    return "INSERT INTO ${table.table} ($columnNames) VALUES ($values);"
}

private fun randomValueFor(field: SQLFieldName<*>): String {
    return when (field.type) {
        Int::class.java     -> Random.nextInt(1, 10_000).toString()
        Integer::class.java     -> Random.nextInt(1, 10_000).toString()
        Long::class.java    -> Random.nextLong(1, 100_000).toString()
        Float::class.java   -> Random.nextFloat().toString()
        Double::class.java  -> Random.nextDouble().toString()
        Boolean::class.java -> Random.nextBoolean().toString()
        String::class.java  -> "'${randomString()}'"
        else                -> throw Exception("Unexpected field type: ${field.type}")
    }
}

private fun randomString(): String {
    val chars = ('a'..'z') + ('A'..'Z')
    return (1..8).map { chars.random() }.joinToString("")
}