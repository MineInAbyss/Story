package io.github.paul1365972.story.key

import org.bukkit.plugin.java.JavaPlugin
import java.io.*

abstract class StreamDataKey<T : Any>(
        plugin: JavaPlugin,
        name: String
) : DataKey<T>(plugin, name) {

    @Throws(IOException::class)
    abstract fun serialize(value: T, oos: ObjectOutputStream)

    @Throws(IOException::class)
    abstract fun deserialize(ois: ObjectInputStream): T

    final override fun serialize(value: T): ByteArray {
        return ByteArrayOutputStream().apply {
            ObjectOutputStream(this).use {
                serialize(value, it)
            }
        }.toByteArray()
    }

    final override fun deserialize(data: ByteArray): T {
        return ByteArrayInputStream(data).run {
            ObjectInputStream(this).use {
                deserialize(it)
            }
        }
    }

    override fun copy(value: T): T = deserialize(serialize(value))
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
