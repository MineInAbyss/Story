package io.github.paul1365972.story.access

import io.github.paul1365972.story.datastore.ObjectDataStore
import io.github.paul1365972.story.key.PersistentDataKeyI

class DataStoreInstanceAccess<T : Any, L>(
        val dataStore: ObjectDataStore<L>,
        val dataKey: PersistentDataKeyI<T>,
        val locationKey: L
) : InstanceAccess<T, L> {
    override fun get(): T? = dataStore.get(dataKey, dataKey.serializer, locationKey)
    override fun set(value: T?) = dataStore.set(dataKey, dataKey.serializer, locationKey, value)
}
