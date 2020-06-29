package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey

open class TransformingPersistentDataStore<L, K>(
        val underlying: DataStore<K>,
        val transformer: (L) -> K
) : PersistentDataStore<L> {
    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, transformer(locationKey))
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: L, value: T?) {
        underlying.set(dataKey, transformer(locationKey), value)
    }

    override fun close() = underlying.close()
}
