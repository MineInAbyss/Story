package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey

class NullableDataStore<L, K>(
        val underlying: StoryDataStore<K>,
        val transformer: (L) -> K?
) : StoryDataStore<L> {
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
