package app.phoenixshell.sql

object DefaultSQLiteEngine: SQLDatabaseEngine {
    override fun onCreate(transaction: SQLTransaction) {
        transaction.exec("create table _versions(id text primary key on conflict replace, version integer);")
        transaction.exec("insert into _versions(id, version) values('current', 0);")
    }

    override fun getCurrentDatabaseVersion(database: SQLConnection): SQLDatabaseVersion {
        //TODO: if there is an error getting the version, we assume null. This may not be the case. Its possible the version query is wrong, find a better way to handle this
        return database.useTransaction { tact ->
            try {
                val result = tact.prepare("select * from _versions where id = 'current';", arrayOf()).executeQuery()

                result.next()
                val ver = result.getInt("version")
                SQLDatabaseVersion.CurrentVersion(ver)
            } catch (e: Exception) {
                SQLDatabaseVersion.EmptyVersion
            }
        }.getOk()
    }

    override fun setCurrentDatabaseVersion(transaction: SQLTransaction, version: Int) {
        transaction.exec("insert into _versions(id, version) values('current', $version);")
    }
}