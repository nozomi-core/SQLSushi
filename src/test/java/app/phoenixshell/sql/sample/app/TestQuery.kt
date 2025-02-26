package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLQueryList
import app.phoenixshell.sql.maps

object TestQuery {
    object User: SQLQueryList() {
        fun insert(QName: String, QBirthYear: Int) = buildQuery<TestSchema.User> { options, schema, statement, binding ->
            with(schema) {
                statement("""
                    insert into $table(
                        $derived,
                        $name, 
                        $birthYear
                    )
                    values(
                        ${binding(derived)},
                        ${binding(name)},
                        ${binding(birthYear)}
                        
                   );
                """).args(

                    name maps QName,
                    birthYear maps QBirthYear,
                    derived maps QBirthYear - 99
                )
            }
        }

        fun getByAge(birthYear: Int) = buildQuery<TestSchema.User> { options, schema, statement, binding ->
            with(schema) {
                statement("""
                    select ${options.selection} from $table where ${this.birthYear} = ${binding(this.birthYear)} limit ${options.limit}
                """).args(

                    this.birthYear maps birthYear
                )
            }
        }
    }
}