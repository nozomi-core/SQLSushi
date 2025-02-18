package app.phoenixshell.sql

import java.sql.PreparedStatement

typealias QueryBuilderFactory<T> = (SQLSelection, T, TemplateBuilder) -> SQLTemplate
typealias TemplateBuilder = (String) -> SQLTemplateBinding

class SQLTemplateBinding {
    fun bind(callback: PreparedStatement.() -> Unit): SQLTemplate {
        TODO()
    }
}

class SQLTemplate {

}

class SQLSelection {

}


open class SQLQueryList {
    fun query(sql: String): String {
        return ""
    }

    fun <T> buildQuery(callback: QueryBuilderFactory<T>): SQLTemplate {
        TODO()
    }
}