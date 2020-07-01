package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKey

abstract class FilterDataStore<L>(
        val underlying: DataStore<L>
) : DataStore<L> {

    override fun <T : Any> get(dataKey: DataKey<T, *>, locationKey: L): T? = underlying.get(dataKey, locationKey)

    override fun <T : Any> set(dataKey: DataKey<T, *>, locationKey: L, value: T?) =
            underlying.set(dataKey, locationKey, value)

    override fun tick() = underlying.tick()

    override fun flush() = underlying.flush()

    override fun close() = underlying.close()
}
