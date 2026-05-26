package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.SQLReadContext
import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.Tables
import app.phoenixshell.sql.UserModel
import app.phoenixshell.sql.WhereBuilder
import app.phoenixshell.sql.WhereQuery
import app.phoenixshell.sql.query.asList

inline fun <reified S: SQLTable> S.where(query: WhereBuilder<S>): WhereQuery<S> {
    return query.using(this)
}

