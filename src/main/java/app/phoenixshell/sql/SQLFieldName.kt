package app.phoenixshell.sql

class SQLFieldName<T>(
    val table: SQLTableName,
    val field: String,
    val javClass: Class<T>
) {
    override fun toString(): String = field
}