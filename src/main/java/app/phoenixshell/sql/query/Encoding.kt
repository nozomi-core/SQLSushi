package app.phoenixshell.sql.query

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import java.sql.PreparedStatement

class PreparedStatementEncoder(
    private val stmt: PreparedStatement
) : AbstractEncoder() {
    private var index = 1

    override val serializersModule = EmptySerializersModule()

    override fun encodeInt(value: Int)         { stmt.setInt(index++, value) }
    override fun encodeLong(value: Long)        { stmt.setLong(index++, value) }
    override fun encodeFloat(value: Float)      { stmt.setFloat(index++, value) }
    override fun encodeDouble(value: Double)    { stmt.setDouble(index++, value) }
    override fun encodeBoolean(value: Boolean)  { stmt.setBoolean(index++, value) }
    override fun encodeString(value: String)    { stmt.setString(index++, value) }
    override fun encodeNull()                   { stmt.setNull(index++, java.sql.Types.NULL) }
    override fun encodeNotNullMark()            { } // skip, handled by encodeNull
}

class ResultSetDecoder(
    private val rs: java.sql.ResultSet,
    private val descriptor: SerialDescriptor
) : AbstractDecoder() {
    private var index = 0

    override val serializersModule = EmptySerializersModule()
    override fun decodeSequentially() = true
    override fun decodeElementIndex(descriptor: SerialDescriptor) = index  // no increment

    private fun columnName() = descriptor.getElementName(index++)  // only increment here

    override fun decodeInt(): Int {
        val name = columnName()
        val value = rs.getObject(name) ?: throw SQLNullDecodingException(
            "Column '$name' is NULL but model field is non-nullable"
        )
        return (value as Number).toInt()
    }

    override fun decodeLong(): Long {
        val name = columnName()
        val value = rs.getObject(name) ?: throw SQLNullDecodingException(
            "Column '$name' is NULL but model field is non-nullable"
        )
        return (value as Number).toLong()
    }

    override fun decodeFloat(): Float {
        val name = columnName()
        val value = rs.getObject(name) ?: throw SQLNullDecodingException(
            "Column '$name' is NULL but model field is non-nullable"
        )
        return (value as Number).toFloat()
    }

    override fun decodeDouble(): Double {
        val name = columnName()
        val value = rs.getObject(name) ?: throw SQLNullDecodingException(
            "Column '$name' is NULL but model field is non-nullable"
        )
        return (value as Number).toDouble()
    }

    override fun decodeBoolean(): Boolean {
        val name = columnName()
        val value = rs.getObject(name) ?: throw SQLNullDecodingException(
            "Column '$name' is NULL but model field is non-nullable"
        )
        return value as Boolean
    }

    override fun decodeString(): String {
        val name = columnName()
        return rs.getString(name) ?: throw SQLNullDecodingException(
            "Column '$name' is NULL but model field is non-nullable"
        )
    }

    override fun decodeNotNullMark(): Boolean {
        val name = descriptor.getElementName(index)  // peek, no increment
        return rs.getObject(name) != null
    }

    override fun decodeNull(): Nothing? {
        index++  // advance past null column
        return null
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor) =
        descriptor.elementsCount
}

class SQLNullDecodingException(message: String) : Exception(message)