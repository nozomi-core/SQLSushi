package app.phoenixshell.sql

interface SQLDatabaseMigration {
    val version: Int
    fun onMigrate(tact: SQLTransaction)
    fun onPostMigrate(tact: SQLTransaction)
}