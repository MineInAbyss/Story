package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey

class DebugPersistentDataStore<L>(
        underlying: PersistentDataStore<L>
) : FilterPersistentDataStore<L>(underlying) {

    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: L): T? {
        println("Fetching ${dataKey.name} at $locationKey")
        return underlying.get(dataKey, locationKey)
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: L, value: T?) {
        println("Putting ${dataKey.name} with data $value at $locationKey")
        underlying.set(dataKey, locationKey, value)
    }
}
