package app.phoenixshell.sql

open class SQLSchema

open class SQLTableName(private val schema: SQLSchema, val table: String) {
    //TODO: Added SQL schema for future

    val id = field<String>("id")

    override fun toString(): String = table

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