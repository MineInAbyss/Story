package io.github.paul1365972.metay.datastore.filters

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore

open class TransformingDataStore<L, K>(
        val underlying: MetayDataStore<in K>,
        val transformer: (L) -> K
) : MetayDataStore<L> {
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, transformer(locationKey))
    }

    override fun <T : Any> put(dataKey: DataKey<T>, locationKey: L, value: T?) {
        underlying.put(dataKey, transformer(locationKey), value)
    }

    override fun close() = underlying.close()
}
