package app.phoenixshell.sql

import java.sql.ResultSet

typealias SQLMapper<T> = ResultSet.() -> T

