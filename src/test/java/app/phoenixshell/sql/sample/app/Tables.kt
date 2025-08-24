package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLSchema
import app.phoenixshell.sql.SQLTable

object Tables: SQLSchema() {
    object User: SQLTable(this,"users") {
        val name = string("name")
        val birthYear = int("birthYear")
        val derived = int("derived")

    }
}