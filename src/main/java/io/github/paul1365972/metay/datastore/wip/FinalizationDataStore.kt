package io.github.paul1365972.metay.datastore.wip

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.MetayDataStore
import io.github.paul1365972.metay.datastore.filters.FilterDataStore

class ProxyedDataStore<L> @JvmOverloads constructor(
        underlying: MetayDataStore<in L>,
        val cacheKeyMapper: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    @Suppress("UNCHECKED_CAST")
    private val cache: Cache<Pair<DataKey<*>, Any?>, Pair<Any?, L>> = CacheBuilder.newBuilder()
            .removalListener<Pair<DataKey<*>, Any?>, Pair<Any?, L>> {
                underlying.set(it.key.first as DataKey<Any>, it.value.second, it.value.first)
            }.build()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return cache.get(dataKey to cacheKeyMapper(locationKey)) {
            underlying.get(dataKey, locationKey) to locationKey
        }.second as T?
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        cache.put(dataKey to cacheKeyMapper(locationKey), value to locationKey)
    }

    override fun close() {
        cache.invalidateAll()
        super.close()
    }
}
