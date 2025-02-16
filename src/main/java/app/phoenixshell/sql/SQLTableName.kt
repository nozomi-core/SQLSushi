package app.phoenixshell.sql

open class SQLTableName(val tableName: String) {
    val id = "id"

    fun field(name: String): SQLFieldName {
        return SQLFieldName(this, name)
    }
}