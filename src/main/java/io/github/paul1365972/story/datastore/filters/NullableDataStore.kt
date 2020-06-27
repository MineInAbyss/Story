package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKey

class NullableDataStore<L, K>(
        val underlying: DataStore<K>,
        val transformer: (L) -> K?
) : DataStore<L> {
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return transformer(locationKey)?.let {
            underlying.get(dataKey, it)
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        transformer(locationKey)?.let {
            underlying.set(dataKey, it, value)
        }
    }

    override fun close() = underlying.close()
}
