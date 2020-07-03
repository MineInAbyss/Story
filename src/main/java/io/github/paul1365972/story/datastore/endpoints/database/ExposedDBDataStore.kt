package io.github.paul1365972.story.datastore.endpoints.database

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKeyI
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedDBDataStore(
        val database: Database,
        tableName: String,
        keyColumn: String = "key",
        bytesColumn: String = "bytes"
) : DataStore<String> {

    private val table = object : Table(tableName) {
        val key = varchar(keyColumn, length = 127)
        val bytes = blob(bytesColumn)

        override val primaryKey = PrimaryKey(key)
    }

    init {
        transaction(database) {
            SchemaUtils.create(table)
        }
    }

    override fun get(dataKey: DataKeyI, locationKey: String): ByteArray? {
        val key = toKey(dataKey, locationKey)
        return transaction(database) {
            table.select {
                table.key.eq(key)
            }.firstOrNull()?.let {
                it[table.bytes].bytes
            }
        }
    }

    override fun set(dataKey: DataKeyI, locationKey: String, data: ByteArray?) {
        val key = toKey(dataKey, locationKey)
        if (data != null) {
            transaction(database) {
                table.replace {
                    it[table.key] = key
                    it[table.bytes] = ExposedBlob(data)
                }
            }
        } else {
            transaction(database) {
                table.deleteWhere {
                    table.key.eq(key)
                }
            }
        }
    }

    private fun toKey(dataKey: DataKeyI, locationKey: Any): String {
        return "${dataKey.namespacedName}:${locationKey}"
    }

}
