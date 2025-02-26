package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLQueryList
import app.phoenixshell.sql.binds

object TestQuery {
    object User: SQLQueryList() {
        fun insert(name: String, birthYear: Int) = buildQuery<TestSchema.User> { options, schema, statement, binding ->
            with(schema) {
                statement("""
                    insert into $tableName(
                        $derivedField,
                        $nameField, 
                        $birthYearField
                    )
                    values(
                        ${binding(derivedField)},
                        ${binding(nameField)},
                        ${binding(birthYearField)}
                        
                   );
                """).bind(

                    nameField binds name,
                    birthYearField binds birthYear,
                    derivedField binds birthYear - 99
                )
            }
        }

        fun getByAge(birthYear: Int) = buildQuery<TestSchema.User> { options, schema, statement, binding ->
            with(schema) {
                statement("""
                    select ${options.selection} from $tableName where $birthYearField = ${binding(birthYearField)} limit ${options.limit}
                """).bind(

                    birthYearField binds birthYear
                )
            }
        }
    }
}