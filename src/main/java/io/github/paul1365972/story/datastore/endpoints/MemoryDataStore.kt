package io.github.paul1365972.story.datastore.endpoints

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey
import java.util.concurrent.ConcurrentHashMap

open class MemoryDataStore<L> : StoryDataStore<L> {

    protected val map = ConcurrentHashMap<Pair<String, L>, ByteArray>()

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return map[dataKey.namespacedName to locationKey]?.let {
            dataKey.deserialize(it)
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        if (value != null)
            map[dataKey.namespacedName to locationKey] = dataKey.serialize(value)
        else
            map.remove(dataKey.namespacedName to locationKey)
    }
}
