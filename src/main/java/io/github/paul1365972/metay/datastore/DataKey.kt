package io.github.paul1365972.metay.datastore

import kotlinx.io.ByteArrayOutputStream
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class DataKey<T : Any>(
        //TODO Non null this, testing only
        val plugin: JavaPlugin?,
        val name: String,
        val serializer: (T) -> ByteArray,
        val deserializer: (ByteArray) -> T
) {
    val namespacedKey: NamespacedKey = NamespacedKey(plugin!!, name)
    val namespacedName: String = "${plugin?.name}:$name"

    constructor(plugin: JavaPlugin?,
                name: String,
                serializer: KSerializer<T>,
                json: Json
    ) : this(
            plugin,
            name,
            { obj -> json.stringify(serializer, obj).toByteArray() },
            { data -> json.parse(serializer, String(data)) }
    )

    constructor(plugin: JavaPlugin?,
                name: String,
                serializer: Serializer<T>,
                deserializer: Deserializer<T>
    ) : this(
            plugin,
            name,
            { obj ->
                ByteArrayOutputStream().apply {
                    ObjectOutputStream(this).use {
                        serializer.apply(obj, it)
                    }
                }.toByteArray()
            },
            { data ->
                ByteArrayInputStream(data).run {
                    ObjectInputStream(this).use {
                        deserializer.apply(it)
                    }
                }
            }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DataKey<*>
        if (namespacedName != other.namespacedName) return false
        return true
    }

    override fun hashCode(): Int = namespacedName.hashCode()

    override fun toString(): String = namespacedName
}

@FunctionalInterface
interface Serializer<T> {
    @Throws(IOException::class)
    fun apply(value: T, oos: ObjectOutputStream)
}

@FunctionalInterface
interface Deserializer<T> {
    @Throws(IOException::class)
    fun apply(ois: ObjectInputStream): T
}
