package app.phoenixshell.sql.sample.app

import kotlinx.serialization.Serializable

@Serializable
data class TestModel(
    val name: String,
    val birthYear: Int
)