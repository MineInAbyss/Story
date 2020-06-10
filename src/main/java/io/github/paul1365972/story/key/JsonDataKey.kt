package io.github.paul1365972.story.key

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.bukkit.plugin.java.JavaPlugin

open class JsonDataKey<T : Any> @JvmOverloads constructor(
        plugin: JavaPlugin,
        name: String,
        private val serializer: KSerializer<T>,
        private val json: Json = Json(JsonConfiguration.Stable)
) : DataKey<T>(plugin, name) {

    final override fun serialize(value: T): ByteArray = json.stringify(serializer, value).toByteArray()

    final override fun deserialize(data: ByteArray): T = json.parse(serializer, String(data))

    override fun copy(value: T): T = deserialize(serialize(value))
}
