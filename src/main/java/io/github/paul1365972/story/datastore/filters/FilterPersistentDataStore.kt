package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey

abstract class FilterPersistentDataStore<L>(
        val underlying: PersistentDataStore<L>
) : PersistentDataStore<L> {

    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, locationKey)
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: L, value: T?) {
        underlying.set(dataKey, locationKey, value)
    }

    override fun close() = underlying.close()
}
