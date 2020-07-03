package io.github.paul1365972.story.serializer

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor

class KtSerializer<T : Any>(
        val copyFunction: T.() -> T,
        val serializer: KSerializer<T>,
        val binaryFormat: BinaryFormat = Cbor.Default
) : StorySerializer<T> {
    override fun serialize(value: T): ByteArray {
        return binaryFormat.dump(serializer, value)
    }

    override fun deserialize(data: ByteArray): T {
        return binaryFormat.load(serializer, data)
    }

    override fun copy(value: T): T = copyFunction(value)
}
