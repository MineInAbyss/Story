package io.github.paul1365972.story.key

import io.github.paul1365972.story.datastore.DataStore
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import org.bukkit.plugin.Plugin

open class KsxDataKey<T : Any, in L>(
        plugin: Plugin,
        name: String,
        dataStore: DataStore<L>,
        val copyFunction: (T) -> T,
        val serializer: KSerializer<T>,
        val binaryFormat: BinaryFormat = Cbor.Default
) : DataKey<T, L>(plugin, name, dataStore) {

    override fun serialize(value: T): ByteArray {
        return binaryFormat.dump(serializer, value)
    }

    override fun deserialize(data: ByteArray): T {
        return binaryFormat.load(serializer, data)
    }

    override fun copy(value: T): T = copyFunction(value)
}
