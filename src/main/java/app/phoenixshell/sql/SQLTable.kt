package app.phoenixshell.sql

open class SQLSchema

open class SQLTable(
    val table: String
) {
    internal val columns = mutableListOf<SQLFieldName<*>>()

    override fun toString(): String = table

    fun int(name: String): SQLFieldName<Int> = field(name)
    fun string(name: String): SQLFieldName<String> = field(name)
    fun long(name: String): SQLFieldName<Long> = field(name)
    fun float(name: String): SQLFieldName<Float> = field(name)
    fun double(name: String): SQLFieldName<Double> = field(name)
    fun boolean(name: String): SQLFieldName<Boolean> = field(name)

    private inline fun <reified T> field(name: String): SQLFieldName<T> {
        return SQLFieldName(name, T::class.java).apply {
            columns.add(this)
        }
    }
}
