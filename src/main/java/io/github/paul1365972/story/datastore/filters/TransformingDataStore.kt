package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKeyI

class TransformingDataStore<L, K>(
        val underlying: DataStore<K>,
        val transformer: (L) -> K
) : DataStore<L> {

    override fun get(dataKey: DataKeyI, locationKey: L): ByteArray? =
            underlying.get(dataKey, transformer(locationKey))

    override fun set(dataKey: DataKeyI, locationKey: L, data: ByteArray?) =
            underlying.set(dataKey, transformer(locationKey), data)

    override fun tick() = underlying.tick()

    override fun flush() = underlying.flush()

    override fun close() = underlying.close()
}
