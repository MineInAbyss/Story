package io.github.paul1365972.metay.storage

import kotlinx.io.ByteArrayOutputStream
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class DataKey<T>(
        val plugin: JavaPlugin?,
        val name: String,
        val serializer: (T) -> ByteArray,
        val deserializer: (ByteArray) -> T
) {

    val namespacedKey: String
        get() {
            return "${plugin?.name}:$name"
        }

    constructor(plugin: JavaPlugin,
                name: String,
                serializer: KSerializer<T>,
                json: Json
    ) : this(
            plugin,
            name,
            { obj -> json.stringify(serializer, obj).toByteArray() },
            { data -> json.parse(serializer, String(data)) }
    )

    constructor(plugin: JavaPlugin,
                name: String,
                serializer: (T, ObjectOutputStream) -> Unit,
                deserializer: (ObjectInputStream) -> T
    ) : this(
            plugin,
            name,
            { obj ->
                ByteArrayOutputStream().apply {
                    ObjectOutputStream(this).use {
                        serializer(obj, it)
                    }
                }.toByteArray()
            },
            { data ->
                ByteArrayInputStream(data).run {
                    ObjectInputStream(this).use {
                        deserializer(it)
                    }
                }
            }
    )

}
