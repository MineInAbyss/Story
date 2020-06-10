package io.github.paul1365972.story.key

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

abstract class DataKey<T : Any>(
        val plugin: JavaPlugin,
        val name: String
) {
    val namespacedKey: NamespacedKey = NamespacedKey(plugin, name)
    val namespacedName: String = "${plugin.name}:$name"

    abstract fun serialize(value: T): ByteArray
    abstract fun deserialize(data: ByteArray): T
    abstract fun copy(value: T): T

    override fun hashCode(): Int = namespacedName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DataKey<*>
        if (namespacedName != other.namespacedName) return false
        return true
    }

    override fun toString(): String = "DataKey(name='$namespacedName')"
}
