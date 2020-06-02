package io.github.paul1365972.metay.storage.endpoints

import io.github.paul1365972.metay.storage.DataKey
import io.github.paul1365972.metay.storage.MetayDataStore

open class MemoryDataStore<L> : MetayDataStore<L> {

    protected val map = mutableMapOf<Pair<String, L>, ByteArray>()

    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        return map[dataKey.namespacedName to locationKey]?.let {
            dataKey.deserializer(it)
        }
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        map[dataKey.namespacedName to locationKey] = dataKey.serializer(value)
    }

    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        map.remove(dataKey.namespacedName to locationKey)
    }
}
