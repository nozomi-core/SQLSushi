package app.phoenixshell.sql

import java.sql.ResultSet

interface ResultDecoder {
    fun <T>decode(kClass: Class<T>, resultSet: ResultSet): T
}