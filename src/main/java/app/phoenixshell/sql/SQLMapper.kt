package app.phoenixshell.sql

import java.sql.ResultSet

//typealias SQLMapper<T> = ResultSet.() -> T

typealias SQLMapper<Schema, Data> = (Schema, ResultSet) -> Data
