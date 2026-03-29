package app.phoenixshell.sql

class SQLFieldName<T>(
    val table: SQLTable,
    val field: String,
    val type: Class<T>
) {
    override fun toString(): String = field
}