package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLQueryList
import app.phoenixshell.sql.binds

object TestQuery {
    object User: SQLQueryList() {
        fun insert(name: String, birthYear: Int) = buildQuery<TestSchema.User> { _, schema, template ->
            with(schema) {
                template("""
                    insert into $tableName($, $) values(?, ?);
                """).bind(

                    nameField binds name,
                    birthYearField binds birthYear
                )
            }
        }

        fun getByAge(birthYear: Int) = buildQuery<TestSchema.User> { selection, schema, template ->
            with(schema) {
                template("""
                    select $selection from $tableName where $ = ?
                """).bind(

                    birthYearField binds birthYear
                )
            }
        }
    }
}