package io.github.paul1365972.metay.storage

import io.github.paul1365972.metay.util.SizedHashMap

class CacheDataStore<L>(
        underlying: MetayDataStore<L>,
        val cacheSize: Int
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
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> remove(dataKey: DataKey<T>, locationKey: L) {
        val key = (dataKey to locationKey) as Pair<DataKey<Any?>, L>
        loaded.remove(key)
        underlying.remove(dataKey, locationKey)
    }

    override fun onClose() {
        loaded.forEach { (k, v) -> save(k.first, k.second, v) }
        super.onClose()
    }

    private fun <T> save(dataKey: DataKey<T>, locationKey: L, value: T) {
        underlying.put(dataKey, locationKey, value)
    }

}
