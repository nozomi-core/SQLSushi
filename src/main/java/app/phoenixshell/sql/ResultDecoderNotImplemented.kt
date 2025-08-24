package app.phoenixshell.sql

import java.sql.ResultSet

object ResultDecoderNotImplemented: ResultDecoder {
    override fun <T> decode(kClass: Class<T>, resultSet: ResultSet): T {
        TODO("Not yet implemented")
    }
}