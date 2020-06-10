package io.github.paul1365972.story.key

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import org.bukkit.plugin.Plugin

open class CborDataKey<T : Any> @JvmOverloads constructor(
        plugin: Plugin,
        name: String,
        private val serializer: KSerializer<T>,
        private val cbor: BinaryFormat = Cbor.Default
) : DataKey<T>(plugin, name) {

    final override fun serialize(value: T): ByteArray = cbor.dump(serializer, value)

    final override fun deserialize(data: ByteArray): T = cbor.load(serializer, data)

    override fun copy(value: T): T = deserialize(serialize(value))
}
