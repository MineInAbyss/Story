package io.github.paul1365972.metay.datastore.caches

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore
import io.github.paul1365972.metay.datastore.filters.FilterDataStore

class WeakRefDataStore<L>(
        underlying: MetayDataStore<in L>
) : FilterDataStore<L>(underlying) {

    @Suppress("UNCHECKED_CAST")
    private val loaded: Cache<Any, Entry<L>> = CacheBuilder.newBuilder()
            .weakKeys()
            .removalListener<Any, Entry<L>> {
                underlying.put(it.value.dataKey as DataKey<Any>, it.value.locationKey, it.key)
            }.build()

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return underlying.get(dataKey, locationKey)?.apply {
            val prev = loaded.get(this) { Entry(dataKey, locationKey) }
            if (prev != null)
                throw RuntimeException("Two identical objects returned by get")
        }
    }

    override fun <T : Any> put(dataKey: DataKey<T>, locationKey: L, value: T?) {
        underlying.put(dataKey, locationKey, value)
    }

    override fun close() {
        loaded.invalidateAll()
        super.close()
    }

    private data class Entry<T>(
            val dataKey: DataKey<*>,
            val locationKey: T
    )
}
