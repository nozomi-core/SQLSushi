package app.phoenixshell.sql

sealed class BlockResult {
    data object Success: BlockResult()
    data object Fail: BlockResult()

    fun assertFail() = assert(this is Fail)
    fun assertSuccess() = assert(this is Success)
}

fun tryTest(callback: () -> Unit): BlockResult {
    try {
        callback()
        return BlockResult.Success
    }catch (e: Exception) {
        return BlockResult.Fail
    }
}