package app.phoenixshell.sql

class SQLFieldName<T>(
    val table: SQLTable,
    val field: String,
    val javClass: Class<T>
) {
    override fun toString(): String = field
}