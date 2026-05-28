package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.SQLTable
import app.phoenixshell.sql.WhereBuilder
import app.phoenixshell.sql.WhereQuery

inline fun <reified S: SQLTable> S.where(query: WhereBuilder<S>): WhereQuery<S> {
    return query.using(this)
}

