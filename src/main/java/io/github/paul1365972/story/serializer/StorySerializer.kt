package io.github.paul1365972.story.serializer

interface StorySerializer<T : Any> {
    fun serialize(value: T): ByteArray
    fun deserialize(data: ByteArray): T
    fun copy(value: T): T = deserialize(serialize(value))
}
