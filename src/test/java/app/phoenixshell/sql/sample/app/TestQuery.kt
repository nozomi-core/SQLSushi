package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.buildTemplate
import app.phoenixshell.sql.maps

object TestQuery {
    object User {
        fun insert(QName: String, QBirthYear: Int) = buildTemplate<Tables.User> { options, schema, statement, bind ->
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

                    name maps QName,
                    birthYear maps QBirthYear,
                    derived maps QBirthYear - 99
                )
            }
        }

        fun getByAge(birthYear: Int) = buildTemplate<Tables.User> { options, schema, statement, bind ->
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