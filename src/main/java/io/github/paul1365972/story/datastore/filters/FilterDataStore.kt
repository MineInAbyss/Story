package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey

abstract class FilterDataStore<L>(
        val underlying: StoryDataStore<in L>
) : StoryDataStore<L> {

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, locationKey)
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        underlying.set(dataKey, locationKey, value)
    }

    override fun close() = underlying.close()
}
