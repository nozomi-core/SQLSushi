import app.phoenixshell.sql.SQLBinding
import app.phoenixshell.sql.SQLContext
import app.phoenixshell.sql.SQLFieldName
import app.phoenixshell.sql.WhereQuery
import app.phoenixshell.sql.query.ResultSetDecoder
import app.phoenixshell.sql.query.SortOrder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import java.sql.Connection
import java.util.Base64

// ─── Cursor ───────────────────────────────────────────────────────────────────

@Serializable
data class CursorFormat(
    val id: String,
    val orderVal: String
)

fun encodeCursor(cursor: CursorFormat): String {
    val json = Json.encodeToString(cursor)
    return Base64.getUrlEncoder().encodeToString(json.toByteArray(Charsets.UTF_8))
}

fun decodeCursor(cursor64: String): CursorFormat? {
    return try {
        val json = Base64.getUrlDecoder().decode(cursor64).toString(Charsets.UTF_8)
        Json.decodeFromString<CursorFormat>(json)
    } catch (e: Exception) {
        null
    }
}

// ─── Page Result ──────────────────────────────────────────────────────────────

data class PageResult<T>(
    val data: List<T>,
    val nextCursor: String?
)

// ─── Paginated Query ──────────────────────────────────────────────────────────



class PaginatedQuery(
    val statement: String,
    val bindings: List<SQLBinding<*>>,
    val orderBy: SQLFieldName<String>,
    val pageId: SQLFieldName<String>,
) {
    inline fun <reified T> execute(
        context: SQLContext,
    ): PageResult<T> {

        var lastId: String? = null
        var lastOrderBy: String? = null

        val rows = context.prepare(statement) { stmt ->
            bindings.forEachIndexed { i, binding ->
                stmt.setObject(i + 1, binding.value)
            }

            stmt.executeQuery().use { rs ->
                buildList {
                    while (rs.next()) {
                        val decoder = ResultSetDecoder(rs, serializer<T>().descriptor)
                        add(serializer<T>().deserialize(decoder))

                        lastId = rs.getString(pageId.field)
                        lastOrderBy = rs.getString(orderBy.field)
                    }
                }
            }
        }

        val nextCursor = createCursor(lastId, lastOrderBy)

        return PageResult(
            data = rows,
            nextCursor = nextCursor?.let { encodeCursor(it) },
        )
    }
}

fun createCursor(lastId: String?, lastOrderBy: String?): CursorFormat? {
    return if (lastId != null && lastOrderBy != null) {
        CursorFormat(
            lastId,
            lastOrderBy
        )
    } else null
}

// ─── Paginate Extension ───────────────────────────────────────────────────────

fun <T> WhereQuery<T>.paginate(
    cursorToken: String?,
    limit: Int = 20,
    orderBy: SQLFieldName<String>,
    pageId: SQLFieldName<String>,
    sortOrder: SortOrder = SortOrder.ASC
): PaginatedQuery {
    val cursor = cursorToken?.let { decodeCursor(it) }

    val op = if (sortOrder == SortOrder.ASC) ">" else "<"

    val cursorClause = if (cursor != null) {
        "AND ($orderBy, $pageId) $op (?, ?)"
    } else ""

    val orderByStatement = """
            ORDER BY $orderBy ${sortOrder.name}, $pageId ${sortOrder.name} LIMIT ?
        """.trimIndent()

    val cursorBindings = buildList<SQLBinding<*>> {
        if(cursorClause.isNotBlank()) {
            add(SQLBinding(orderBy, cursor?.orderVal!!))
            add(SQLBinding(pageId, cursor.id))
        }
        add(SQLBinding(SQLFieldName.int("limit"), limit))
    }

    val select = """
        select * from $table
    """.trimIndent()

    return PaginatedQuery(
        statement = "$select ${statement.trimIndent()} $cursorClause $orderByStatement",
        bindings = bindings + cursorBindings,
        orderBy = orderBy,
        pageId = pageId
    )
}