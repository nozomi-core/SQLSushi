package app.phoenixshell.sql

class SQLFieldName<T>(
    val field: String,
    val type: Class<T>
) {
    override fun toString(): String = field

    companion object {
        fun int(name: String) = SQLFieldName(name, Int::class.java)
    }
}