package app.phoenixshell.sql

class SQLFieldName(
    val table: SQLTableName,
    val field: String
) {
    override fun toString(): String = field
}