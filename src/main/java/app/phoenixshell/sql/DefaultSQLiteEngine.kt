package app.phoenixshell.sql

object DefaultSQLiteEngine: SQLDatabaseEngine {
    override fun onCreate(database: SQLContext) {
        database.exec("create table _versions(id text primary key on conflict replace, version integer);")
        database.exec("insert into _versions(id, version) values('current', 0);")
    }

    override fun getCurrentDatabaseVersion(database: SQLContext): SQLDatabaseVersion {
        //TODO: if there is an error getting the version, we assume null. This may not be the case. Its possible the version query is wrong, find a better way to handle this
        return try {
            val version = database.prepare("select * from _versions where id = 'current';") { stmt ->
                val result = stmt.executeQuery()

                result.next()
                result.getInt("version")
            }

            SQLDatabaseVersion.CurrentVersion(version)
        } catch (e: Exception) {
            e.printStackTrace()
            SQLDatabaseVersion.EmptyVersion
        }
    }

    override fun setCurrentDatabaseVersion(database: SQLContext, version: Int) {
        database.exec("insert into _versions(id, version) values('current', $version);")
    }
}