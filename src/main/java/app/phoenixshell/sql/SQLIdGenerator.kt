package app.phoenixshell.sql

interface SQLIdGenerator<T> {
    fun generateId(): T
}