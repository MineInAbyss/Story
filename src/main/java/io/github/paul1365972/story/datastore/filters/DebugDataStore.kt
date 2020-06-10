package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey

class DebugDataStore<L>(
        val underlying: StoryDataStore<L>
) : StoryDataStore<L> {

    override fun close() = underlying.close()
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        println("Fetching ${dataKey.name} at $locationKey")
        return underlying.get(dataKey, locationKey)
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        println("Putting ${dataKey.name} with data $value at $locationKey")
        underlying.set(dataKey, locationKey, value)
    }
}
