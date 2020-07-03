package io.github.paul1365972.story.key

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

open class DataKey(
        override val plugin: Plugin,
        override val name: String
) : DataKeyI {
    override val namespacedName: String = super.namespacedName
    override val namespacedKey: NamespacedKey = super.namespacedKey

    override fun hashCode(): Int = namespacedName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataKey) return false
        if (namespacedName != other.namespacedName) return false
        return true
    }

    override fun toString(): String = "DataKey(name='$namespacedName')"
}
