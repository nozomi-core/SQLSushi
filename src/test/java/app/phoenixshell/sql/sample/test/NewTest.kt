package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.SQLReadContext
import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.UserWhere
import app.phoenixshell.sql.WhereBuilder
import app.phoenixshell.sql.WhereQuery
import app.phoenixshell.sql.query.exec

inline fun <reified T: SQLTable> T.select(query: WhereBuilder<T>): WhereQuery<T> {
    return query.using(this)
}

fun TestThis (context: SQLReadContext) {
    Tables.User
        .select(UserWhere.getAll())
        .exec<UserModel>(context)
}