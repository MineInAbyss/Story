package io.github.paul1365972.metay.datastore.filter

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore

open class TransformingDataStore<L, K>(
        val underlying: MetayDataStore<K>,
        val transformer: (L) -> K
) : MetayDataStore<L> {
    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, transformer(locationKey))
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        underlying.put(dataKey, transformer(locationKey), value)
    }

    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        underlying.remove(dataKey, transformer(locationKey))
    }

    override fun close() = underlying.close()
}
