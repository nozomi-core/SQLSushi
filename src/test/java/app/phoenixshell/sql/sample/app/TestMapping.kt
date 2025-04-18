package app.phoenixshell.sql.sample.app

import app.phoenixshell.sql.SQLMapper

val UserMapping: SQLMapper<TestSchema.User, TestModel> = {
    with(schema) {
        TestModel(
            name = get(name),
            birthYear = get(birthYear)
        )
    }
}