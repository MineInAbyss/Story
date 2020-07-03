package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.ObjectDataStore
import io.github.paul1365972.story.key.DataKeyI
import io.github.paul1365972.story.serializer.StorySerializer

class NullableDataStore<L, K>(
        val underlying: ObjectDataStore<K>,
        val transformer: (L) -> K?
) : ObjectDataStore<L> {

    override fun <T : Any> get(dataKey: DataKeyI, serializer: StorySerializer<T>, locationKey: L) =
            transformer(locationKey)?.let {
                underlying.get(dataKey, serializer, it)
            }

    override fun get(dataKey: DataKeyI, locationKey: L) = transformer(locationKey)?.let {
        underlying.get(dataKey, it)
    }

    override fun <T : Any> set(dataKey: DataKeyI, serializer: StorySerializer<T>, locationKey: L, value: T?) {
        transformer(locationKey)?.let {
            underlying.set(dataKey, serializer, it, value)
        }
    }

    override fun set(dataKey: DataKeyI, locationKey: L, data: ByteArray?) {
        transformer(locationKey)?.let {
            underlying.set(dataKey, it, data)
        }
    }

    override fun tick() = underlying.tick()

    override fun flush() = underlying.flush()

    override fun close() = underlying.close()
}
