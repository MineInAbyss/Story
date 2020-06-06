package io.github.paul1365972.metay.datastore.filter

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore
import io.github.paul1365972.metay.util.SizedHashMap

class CacheDataStore<L> @JvmOverloads constructor(
        underlying: MetayDataStore<in L>,
        val cacheSize: Int,
        val cacheKeyMapper: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    private val loaded = SizedHashMap<Pair<DataKey<Any?>, Any?>, Pair<L, Any?>>(cacheSize) { k, v -> save(k, v) }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        val key = (dataKey to cacheKeyMapper(locationKey)) as Pair<DataKey<Any?>, Any?>
        loaded[key]?.run {
            return this as T
        }
        return underlying.get(dataKey, locationKey)?.also {
            loaded[key] = locationKey to it
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        val key = (dataKey to cacheKeyMapper(locationKey)) as Pair<DataKey<Any?>, Any?>
        loaded[key] = locationKey to value
        underlying.put(dataKey, locationKey, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        val key = (dataKey to cacheKeyMapper(locationKey)) as Pair<DataKey<Any?>, Any?>
        loaded.remove(key)
        underlying.remove(dataKey, locationKey)
    }

    override fun close() {
        loaded.forEach { (k, v) -> save(k, v) }
        super.close()
        loaded.clear()
    }

    private fun <T> save(key: Pair<DataKey<T>, Any?>, value: Pair<L, T>) {
        underlying.put(key.first, value.first, value.second)
    }
}
