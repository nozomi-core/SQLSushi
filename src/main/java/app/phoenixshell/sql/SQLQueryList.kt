package app.phoenixshell.sql

import java.sql.PreparedStatement

typealias QueryBuilderFactory<T> = (SQLSelection, T, TemplateBuilder) -> SQLTemplate<T>
typealias TemplateBuilder = (String) -> SQLTemplateBinding

class SQLTemplateBinding {
    fun <T> bind(callback: PreparedStatement.() -> Unit): SQLTemplate<T> {
        TODO()
    }
}

class SQLTemplate<T> {

}

class SQLSelection {

}


open class SQLQueryList {
    fun query(sql: String): String {
        return ""
    }

    fun <T> buildQuery(callback: QueryBuilderFactory<T>): SQLTemplate<T> {
        TODO()
    }
}