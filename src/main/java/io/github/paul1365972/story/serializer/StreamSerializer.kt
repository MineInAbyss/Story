package io.github.paul1365972.story.serializer

import java.io.*

abstract class StreamSerializer<T : Any> : StorySerializer<T> {
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
}
