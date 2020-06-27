package io.github.paul1365972.story.key

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

open class DataKey<T : Any>(
        val plugin: Plugin,
        val name: String
) {
    val namespacedKey: NamespacedKey = NamespacedKey(plugin, name)
    val namespacedName: String = "${plugin.name}:$name"

    override fun hashCode(): Int = namespacedName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataKey<*>) return false
        if (namespacedName != other.namespacedName) return false
        return true
    }

    override fun toString(): String = "DataKey(name='$namespacedName')"
}
