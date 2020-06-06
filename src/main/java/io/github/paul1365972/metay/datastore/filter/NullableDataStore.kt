package io.github.paul1365972.metay.datastore.filter

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore

open class NullableDataStore<L, K>(
        val underlying: MetayDataStore<K>,
        val transformer: (L) -> K?
) : MetayDataStore<L> {
    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        return transformer(locationKey)?.let {
            underlying.get(dataKey, it)
        }
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        transformer(locationKey)?.let {
            underlying.put(dataKey, it, value)
        }
    }

    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        transformer(locationKey)?.let {
            underlying.remove(dataKey, it)
        }
    }

    override fun close() = underlying.close()
}
