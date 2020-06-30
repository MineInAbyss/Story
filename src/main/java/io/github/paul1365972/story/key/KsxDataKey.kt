package io.github.paul1365972.story.key

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import org.bukkit.plugin.Plugin

open class KsxDataKey<T : Any>(
        plugin: Plugin,
        name: String,
        val copyFunction: (T) -> T,
        val serializer: KSerializer<T>,
        val binaryFormat: BinaryFormat = Cbor.Default
) : PersistentDataKey<T>(plugin, name) {

    override fun serialize(value: T): ByteArray {
        return binaryFormat.dump(serializer, value)
    }

    override fun deserialize(data: ByteArray): T {
        return binaryFormat.load(serializer, data)
    }

    override fun copy(value: T): T = copyFunction(value)

    override fun hashCode(): Int = namespacedName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KsxDataKey<*>) return false
        if (namespacedName != other.namespacedName) return false
        return true
    }

    override fun toString(): String = "DataKey(name='$namespacedName')"
}
