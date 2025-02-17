package app.phoenixshell.sql

open class SQLTableName(val tableName: String) {
    val id = field("id")

    override fun toString(): String = tableName

    fun field(name: String): SQLFieldName {
        return SQLFieldName(this, name)
    }
}