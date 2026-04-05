package app.phoenixshell.sql

class SQLFieldName<T>(
    val field: String,
    val type: Class<T>
) {
    override fun toString(): String = field

    companion object {
        fun int(name: String) = SQLFieldName<Int>(name, Int::class.java)
    }
}