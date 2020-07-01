package io.github.paul1365972.story.key

import io.github.paul1365972.story.datastore.DataStore
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

abstract class DataKey<T : Any, in L>(
        val plugin: Plugin,
        val name: String,
        val defaultDataStore: DataStore<L>
) {
    val namespacedName: String = "${plugin.name}:$name"
    val namespacedKey: NamespacedKey = NamespacedKey(plugin, name)

    abstract fun serialize(value: T): ByteArray

    abstract fun deserialize(data: ByteArray): T

    open fun copy(value: T): T = deserialize(serialize(value))

    override fun hashCode(): Int = namespacedName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataKey<*, *>) return false
        if (namespacedName != other.namespacedName) return false
        return true
    }

    override fun toString(): String = "DataKey(name='$namespacedName')"
}
