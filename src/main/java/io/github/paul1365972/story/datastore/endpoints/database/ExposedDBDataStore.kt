package io.github.paul1365972.story.datastore.endpoints.database

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.DataKey
import io.github.paul1365972.story.key.PersistentDataKey
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedDBDataStore(
        val database: Database,
        tableName: String,
        keyColumn: String = "key",
        bytesColumn: String = "bytes"
) : PersistentDataStore<String> {

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

    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: String): T? {
        val key = toKey(dataKey, locationKey)
        return transaction(database) {
            table.select {
                table.key.eq(key)
            }.firstOrNull()?.let {
                dataKey.deserialize(it[table.bytes].bytes)
            }
        }
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: String, value: T?) {
        val key = toKey(dataKey, locationKey)
        if (value != null) {
            transaction(database) {
                table.replace {
                    it[table.key] = key
                    it[table.bytes] = ExposedBlob(dataKey.serialize(value))
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

    private fun toKey(dataKey: DataKey<*>, locationKey: Any): String {
        return "${dataKey.namespacedName}:${locationKey}"
    }

}
