package io.github.paul1365972.story.key

import io.github.paul1365972.story.datastore.DataStore
import org.bukkit.plugin.Plugin
import java.io.*

abstract class StreamDataKey<T : Any, in L>(
        plugin: Plugin,
        name: String,
        dataStore: DataStore<L>
) : DataKey<T, L>(plugin, name, dataStore) {

    @Throws(IOException::class)
    abstract fun serialize(value: T, oos: ObjectOutputStream)

    @Throws(IOException::class)
    abstract fun deserialize(ois: ObjectInputStream): T

    final override fun serialize(value: T): ByteArray {
        return ByteArrayOutputStream().apply {
            ObjectOutputStream(this).use {
                serialize(value, it)
            }
        }.toByteArray()
    }

    final override fun deserialize(data: ByteArray): T {
        return ByteArrayInputStream(data).run {
            ObjectInputStream(this).use {
                deserialize(it)
            }
        }
    }
}
