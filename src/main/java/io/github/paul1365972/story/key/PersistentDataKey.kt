package io.github.paul1365972.story.key

import org.bukkit.plugin.Plugin

abstract class PersistentDataKey<T : Any>(
        plugin: Plugin,
        name: String
) : DataKey<T>(plugin, name) {

    abstract fun serialize(value: T): ByteArray

    abstract fun deserialize(data: ByteArray): T

    open fun copy(value: T): T = deserialize(serialize(value))
}
