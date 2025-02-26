package app.phoenixshell.sql.sample.test

import app.phoenixshell.sql.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

object ExampleSchema: SQLSchema() {
    object Movie: SQLTableName(this, "movie") {
        val titleField = string("title")
        val yearField = int("year")
    }
}

object ExampleQuery: SQLQueryList() {
    fun insertMovie(title: String) = buildQuery<ExampleSchema.Movie> { _, schema, statement, binding ->
        with(schema) {
            statement("""
                insert into $tableName value of $titleField = ${binding(titleField)} where createdAt = ${binding(
                titleField
            )}
            """.trimIndent())
                .bind(
                    titleField binds title
                )
        }
    }
}
class TestNamingBinding {

    @Test
    fun testNamedBinding() {
        val template = ExampleQuery.insertMovie("EpicShow") as SQLTemplate.Syntax

        val buildTemplate = BuildTemplate()
        val options = QueryOptions(Selection(arrayOf()))

        val binding = template.factory(options, ExampleSchema.Movie, buildTemplate::statement, buildTemplate::binding) as SQLTemplate.Binding

        assertEquals("insert into movie value of title = ? where createdAt = ?", binding.sqlTemplate)
        assertEquals(1, binding.bindingValueMap.size)
        assertEquals(2, buildTemplate.bindingPlaceHolders.size)
    }


    @Test
    fun testExampleScript() {
        val order = BindOrder()

        val template = "insert into popcorn, ? title = ${order.place(ExampleSchema.Movie.titleField)}, when year = ${order.place(
            ExampleSchema.Movie.yearField
        )}"

        val listing = order.list.joinToString(separator = ",")



        //assertEquals("insert into movie value of title = :title", template)
        assertEquals("title,year", listing)
    }
}


class BindOrder {
    val list = mutableListOf<String>()

    fun place(field: SQLFieldName<*>): String {
        list.add(field.field)
        return ":${field.field}"
    }
}