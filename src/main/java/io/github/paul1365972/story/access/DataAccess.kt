package io.github.paul1365972.story.access

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey

class DataAccess<T : Any, in L>(
        val dataKey: DataKey<T>,
        val dataStore: StoryDataStore<L>
) : DataAccessI<T, L> {
    override fun get(locationKey: L): T? = dataStore.get(dataKey, locationKey)

    override fun set(locationKey: L, value: T?) = dataStore.set(dataKey, locationKey, value)
}
