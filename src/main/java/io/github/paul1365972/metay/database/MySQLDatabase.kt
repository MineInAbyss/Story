package io.github.paul1365972.metay.database

import java.sql.Connection

class MySQLDatabase(
        val connection: Connection,
        tableName: String,
        keyColumn: String = "key",
        bytesColumn: String = "bytes"
) : BatchDatabase {

    //TODO I have no clue how mysql works use with great care
    //TODO This code is untested
    //TODO Add transactions? Maybe set autocommit false
    //TODO Insert's REPLACE works but is bad for performance and legacy (make "insert on duplicate key update" work)

    private val selectStatement = connection.prepareStatement("SELECT $bytesColumn FROM $tableName WHERE $keyColumn = ?;")
    private val insertStatement = connection.prepareStatement("REPLACE INTO $tableName ($keyColumn, $bytesColumn) VALUES (?, ?);")

    init {
        val safetyRegex = Regex("^[a-zA-Z_]+\$")
        listOf(tableName, keyColumn, bytesColumn).all { it matches safetyRegex }
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

    override fun get(key: String): ByteArray? {
        selectStatement.setString(1, key)
        val resultset = selectStatement.executeQuery()
        if (resultset.first())
            return resultset.getBytes(1)
        return null
    }

    override fun putBatch(data: Map<String, ByteArray>) {
        data.forEach { (k, v) ->
            selectStatement.setString(1, k)
            selectStatement.setBytes(2, v)
            selectStatement.addBatch()
        }
        selectStatement.executeBatch()
    }

    override fun onClose() {
        selectStatement.close()
        insertStatement.close()
    }
}
