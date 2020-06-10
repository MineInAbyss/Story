package io.github.paul1365972.story.datastore.caches

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.datastore.filters.FilterDataStore
import io.github.paul1365972.story.key.DataKey

class CacheDataStore<L> @JvmOverloads constructor(
        underlying: StoryDataStore<in L>,
        val cacheSize: Int,
        val cacheKeyMapper: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    @Suppress("UNCHECKED_CAST")
    private val cache: Cache<Pair<DataKey<*>, Any?>, Pair<L, Any?>> = CacheBuilder.newBuilder()
            .maximumSize(cacheSize.toLong())
            .removalListener<Pair<DataKey<*>, Any?>, Pair<L, Any?>> {
                underlying.set(it.key.first as DataKey<Any>, it.value.first, it.value.second)
            }.build()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        return cache.get(dataKey to cacheKeyMapper(locationKey)) {
            locationKey to underlying.get(dataKey, locationKey)
        }?.let { dataKey.copy(it as T) }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        cache.put(dataKey to cacheKeyMapper(locationKey), locationKey to value)
    }

    override fun close() {
        cache.invalidateAll()
        super.close()
    }
}
