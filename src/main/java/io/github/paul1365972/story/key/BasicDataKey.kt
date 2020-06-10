package io.github.paul1365972.story.key

import org.bukkit.plugin.Plugin

class BasicDataKey<T : Any> @JvmOverloads constructor(
        plugin: Plugin,
        name: String,
        private val serializer: (T) -> ByteArray,
        private val deserializer: (ByteArray) -> T,
        private val copyer: ((T) -> T) = { deserializer(serializer(it)) }
) : DataKey<T>(plugin, name) {

    override fun serialize(value: T): ByteArray = serializer(value)

    override fun deserialize(data: ByteArray): T = deserializer(data)

    override fun copy(value: T): T = copyer(value)
}
