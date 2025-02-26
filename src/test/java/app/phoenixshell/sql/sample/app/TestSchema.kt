package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLSchema
import app.phoenixshell.sql.SQLTableName

object TestSchema: SQLSchema() {
    object User: SQLTableName(this,"users") {
        val name = string("name")
        val birthYear = int("birth_year")
        val derived = int("derived")

    }

    object Post: SQLTableName(this, "posts") {
        val title = string("title")
        val description = string("description")
        val createdAt = long("created_at")
    }
}