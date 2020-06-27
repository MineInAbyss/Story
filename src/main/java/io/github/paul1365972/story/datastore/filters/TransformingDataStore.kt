package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKey

open class TransformingDataStore<L, K>(
        val underlying: DataStore<K>,
        val transformer: (L) -> K
) : DataStore<L> {
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, transformer(locationKey))
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        underlying.set(dataKey, transformer(locationKey), value)
    }

    override fun close() = underlying.close()
}
