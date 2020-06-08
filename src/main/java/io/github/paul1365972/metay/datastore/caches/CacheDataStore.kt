package io.github.paul1365972.metay.datastore.caches

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore
import io.github.paul1365972.metay.datastore.filters.FilterDataStore

class CacheDataStore<L> @JvmOverloads constructor(
        underlying: MetayDataStore<in L>,
        val cacheSize: Int,
        val cacheKeyMapper: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    @Suppress("UNCHECKED_CAST")
    private val cache: Cache<Pair<DataKey<*>, Any?>, Pair<L, Any?>> = CacheBuilder.newBuilder()
            .maximumSize(cacheSize.toLong())
            .removalListener<Pair<DataKey<*>, Any?>, Pair<L, Any?>> {
                underlying.put(it.key.first as DataKey<Any>, it.value.first, it.value.second)
            }.build()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return cache.get(dataKey to cacheKeyMapper(locationKey)) {
            locationKey to underlying.get(dataKey, locationKey)
        }.second as T?
    }

    override fun <T : Any> put(dataKey: DataKey<T>, locationKey: L, value: T?) {
        cache.put(dataKey to cacheKeyMapper(locationKey), locationKey to value)
    }

    override fun close() {
        cache.invalidateAll()
        super.close()
    }
}
