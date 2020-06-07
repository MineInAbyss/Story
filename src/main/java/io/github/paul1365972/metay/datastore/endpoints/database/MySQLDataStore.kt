package io.github.paul1365972.metay.datastore.endpoints.database

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore
import java.sql.Connection

class MySQLDataStore(
        val connection: Connection,
        tableName: String,
        keyColumn: String = "key",
        bytesColumn: String = "bytes"
) : MetayDataStore<String> {

    //TODO I have no clue how mysql works use with great care
    //TODO This code is untested
    //TODO Add transactions? Maybe set autocommit false
    //TODO Insert's REPLACE works but is bad for performance and legacy (make "insert on duplicate key update" work)
    //TODO Implement batching

    private val selectStatement = connection.prepareStatement("SELECT $bytesColumn FROM $tableName WHERE $keyColumn IN (?);")
    private val insertStatement = connection.prepareStatement("REPLACE INTO $tableName ($keyColumn, $bytesColumn) VALUES (?, ?);")
    private val deleteStatement = connection.prepareStatement("DELETE FROM $tableName WHERE $keyColumn IN (?);")

    init {
        val safetyRegex = Regex("^[a-zA-Z_]+\$")
        if (!listOf(tableName, keyColumn, bytesColumn).all { it matches safetyRegex })
            throw IllegalArgumentException("Provided sql names are not safe")
        connection.createStatement().use {
            it.execute("""
            CREATE TABLE IF NOT EXISTS `$tableName` (
            	`$keyColumn` varchar(255) NOT NULL,
            	`$bytesColumn` varbinary NOT NULL 
            	PRIMARY KEY( `$keyColumn` )
            );
        """.trimIndent())
        }
    }

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: String): T? {
        selectStatement.setString(1, toKey(dataKey, locationKey))
        val resultset = selectStatement.executeQuery()
        if (resultset.first())
            return dataKey.deserializer(resultset.getBytes(1))
        return null
    }

    override fun <T : Any> put(dataKey: DataKey<T>, locationKey: String, value: T?) {
        if (value != null) {
            selectStatement.setString(1, toKey(dataKey, locationKey))
            selectStatement.setBytes(2, dataKey.serializer(value))
            selectStatement.executeUpdate()
        } else {
            selectStatement.setString(1, toKey(dataKey, locationKey))
            selectStatement.executeUpdate()
        }
    }

    override fun close() {
        selectStatement.close()
        insertStatement.close()
        deleteStatement.close()
    }

    // TODO
    fun <T : Any> putBatch(data: Map<Pair<DataKey<T>, String>, T>) {
        data.forEach { (k, v) ->
            selectStatement.setString(1, toKey(k.first, k.second))
            selectStatement.setBytes(2, k.first.serializer(v))
            selectStatement.addBatch()
        }
        selectStatement.executeBatch()
    }

    private fun toKey(dataKey: DataKey<*>, locationKey: Any): String {
        return "${dataKey.namespacedName}:${locationKey}"
    }

}
