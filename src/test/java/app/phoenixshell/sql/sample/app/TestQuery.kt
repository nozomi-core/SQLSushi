package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.buildQuery
import app.phoenixshell.sql.maps

object TestQuery {
    object User {
        fun insert(qName: String, qBirthYear: Int) = buildQuery(Tables.User) { options, schema, statement, bind ->
            with(schema) {
                statement("""
                    insert into $table(
                        $derived,
                        $name, 
                        $birthYear
                    )
                    values(
                        ${bind(derived)},
                        ${bind(name)},
                        ${bind(birthYear)}
                        
                   );
                """).args(

                    name maps qName,
                        birthYear maps qBirthYear,
                    derived maps qBirthYear - 99
                )
            }
        }

        fun getByAge(birthYear: Int) = buildQuery(Tables.User) { options, schema, statement, bind ->
            with(schema) {
                statement("""
                    select ${options.selection} from $table where ${this.birthYear} = ${bind(this.birthYear)} limit ${options.limit}
                """).args(
                    this.birthYear maps birthYear
                )
            }
        }
    }
}