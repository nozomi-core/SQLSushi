package app.phoenixshell.sql.data

import java.sql.ResultSet

interface ResultDecoder {
    fun <T>decode(kClass: Class<T>, resultSet: ResultSet): T
}