package io.github.paul1365972.metay.storage

import io.github.paul1365972.metay.util.SizedHashMap

class CacheDataStore<L>(
        underlying: MetayDataStore<L>,
        val cacheSize: Int
) : FilterDataStore<L>(underlying) {
    private val loaded = SizedHashMap<String, Entry<Any?, L>>(cacheSize) { _, v -> save(v)}

    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        val dbKey = toKey(dataKey, locationKey)
        loaded[dbKey]?.run {
            @Suppress("UNCHECKED_CAST")
            return this.value as T
        }
        return underlying.get(dataKey, locationKey)?.also {
            put(dataKey, locationKey, it)
        }
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        val dbKey = toKey(dataKey, locationKey)
        @Suppress("UNCHECKED_CAST")
        loaded[dbKey] = Entry(dataKey, locationKey, value) as Entry<Any?, L>
    }

    override fun onClose() {
        loaded.values.forEach(this::save)
        super.onClose()
    }

    private fun save(entry: Entry<Any?, L>) {
        underlying.put(entry.dataKey, entry.locationKey, entry.value)
    }

    class Entry<T, L>(
            val dataKey: DataKey<T>,
            val locationKey: L,
            val value: T
    )
}
