package app.phoenixshell.sql

/* Convert All '$' with the array values. Will throw error if any Array<String> variables are not alpha_numeric
* Alpha number requirement is for security to ensure only valid variable names are used */
fun replaceAlphaNumVariables(str: String, sqlFields: Array<SQLFieldName<*>>): String {
    val replacements = sqlFields.map { it.field }

    replacements.forEach {
        requireAlphaNum(it)
    }

    var result = str
    var replacementIndex = 0

    // Replace each dollar sign with the next replacement in the array
    result = result.replace(Regex("\\$")) {
        if (replacementIndex < replacements.size) {
            replacements[replacementIndex++]
        } else {
            // If there are more dollar signs than replacements, just leave it
            "$"
        }
    }
    return result
}

/* Require Alpha num or throw Exception */
private fun requireAlphaNum(str: String) {
    val matches = str.matches("^[a-zA-Z0-9_]*$".toRegex())
    if(!matches) {
        throw Exception("requires only alpha numeric characters")
    }
}
