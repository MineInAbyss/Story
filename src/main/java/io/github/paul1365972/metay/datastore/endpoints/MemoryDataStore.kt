package io.github.paul1365972.metay.datastore.endpoints

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore

open class MemoryDataStore<L> : MetayDataStore<L> {

    protected val map = mutableMapOf<Pair<String, L>, ByteArray>()

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return map[dataKey.namespacedName to locationKey]?.let {
            dataKey.deserializer(it)
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        if (value != null)
            map[dataKey.namespacedName to locationKey] = dataKey.serializer(value)
        else
            map.remove(dataKey.namespacedName to locationKey)
    }
}
