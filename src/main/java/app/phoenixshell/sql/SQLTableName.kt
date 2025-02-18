package app.phoenixshell.sql

open class SQLTableName(val tableName: String) {
    val id = field<String>("id")

    override fun toString(): String = tableName

    fun int(name: String): SQLFieldName<Int> = field(name)
    fun string(name: String): SQLFieldName<String> = field(name)
    fun long(name: String): SQLFieldName<Long> = field(name)
    fun float(name: String): SQLFieldName<Float> = field(name)
    fun double(name: String): SQLFieldName<Double> = field(name)
    fun boolean(name: String): SQLFieldName<Boolean> = field(name)

    fun <T> map(mapper: SQLTableName.() -> T): T {
        return mapper(this)
    }

    private inline fun <reified T> field(name: String): SQLFieldName<T> {
        return SQLFieldName(this, name, T::class.java)
    }
}