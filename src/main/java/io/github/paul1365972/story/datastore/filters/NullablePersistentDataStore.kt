package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey

class NullablePersistentDataStore<L, K>(
        val underlying: PersistentDataStore<K>,
        val transformer: (L) -> K?
) : PersistentDataStore<L> {
    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: L): T? {
        return transformer(locationKey)?.let {
            underlying.get(dataKey, it)
        }
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: L, value: T?) {
        transformer(locationKey)?.let {
            underlying.set(dataKey, it, value)
        }
    }

    override fun close() = underlying.close()
}
