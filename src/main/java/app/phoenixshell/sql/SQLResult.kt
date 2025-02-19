package app.phoenixshell.sql

sealed class SQLResult<out T> {
    class Ok<out T> internal constructor(val value: T): SQLResult<T>()
    class Fail internal constructor(val exception: Exception): SQLResult<Nothing>()

    fun getOrThrow(): T {
        return when(this) {
            is Fail -> throw exception
            is Ok -> value
        }
    }
}