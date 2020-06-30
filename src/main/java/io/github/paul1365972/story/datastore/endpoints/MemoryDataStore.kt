package io.github.paul1365972.story.datastore.endpoints

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKey
import java.util.concurrent.ConcurrentHashMap

class MemoryDataStore<L> : DataStore<L> {

    protected val map = ConcurrentHashMap<Pair<String, L>, Any>()

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        @Suppress("UNCHECKED_CAST")
        return map[dataKey.namespacedName to locationKey] as T?
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        val key = dataKey.namespacedName to locationKey
        if (value != null)
            map[key] = value
        else
            map.remove(key)
    }

    override fun close() = map.clear()
}
