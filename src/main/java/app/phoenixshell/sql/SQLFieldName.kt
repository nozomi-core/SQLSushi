package app.phoenixshell.sql

class SQLFieldName(
    private val table: SQLTableName,
    private val field: String
) {
    override fun toString(): String = field
}