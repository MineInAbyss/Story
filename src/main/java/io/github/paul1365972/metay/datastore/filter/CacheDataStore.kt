package io.github.paul1365972.metay.datastore.filter

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore
import io.github.paul1365972.metay.util.SizedHashMap

class CacheDataStore<L> @JvmOverloads constructor(
        underlying: MetayDataStore<L>,
        val cacheSize: Int,
        val cacheKey: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    private val loaded = SizedHashMap<Pair<DataKey<Any?>, L>, Any?>(cacheSize) { k, v -> save(k.first, k.second, v) }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        val key = (dataKey to locationKey) as Pair<DataKey<Any?>, L>
        loaded[key]?.run {
            return this as T
        }
        return underlying.get(dataKey, locationKey)?.also {
            loaded[key] = it
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        val key = (dataKey to locationKey) as Pair<DataKey<Any?>, L>
        loaded[key] = value
        underlying.put(dataKey, locationKey, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        val key = (dataKey to locationKey) as Pair<DataKey<Any?>, L>
        loaded.remove(key)
        underlying.remove(dataKey, locationKey)
    }

    override fun close() {
        loaded.forEach { (k, v) -> save(k.first, k.second, v) }
        super.close()
        loaded.clear()
    }

    private fun <T> save(dataKey: DataKey<T>, locationKey: L, value: T) {
        underlying.put(dataKey, locationKey, value)
    }

}
