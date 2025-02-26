package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLSchema
import app.phoenixshell.sql.SQLTableName

object TestSchema: SQLSchema() {
    object User: SQLTableName(this,"users") {
        val nameField = string("name")
        val birthYearField = int("birth_year")
        val derivedField = int("derived")

    }

    object Post: SQLTableName(this, "posts") {
        val title = string("title")
        val description = string("description")
        val createAt = long("created_at")
    }
}