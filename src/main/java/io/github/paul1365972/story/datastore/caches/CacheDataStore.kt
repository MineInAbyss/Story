package io.github.paul1365972.story.datastore.caches

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.datastore.filters.FilterDataStore
import io.github.paul1365972.story.key.DataKey
import java.util.concurrent.ExecutionException

class CacheDataStore<L> @JvmOverloads constructor(
        underlying: StoryDataStore<in L>,
        val cacheSize: Int,
        val cacheKeyMapper: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    @Suppress("UNCHECKED_CAST")
    private val cache: LoadingCache<Pair<DataKey<*>, LocationWrapper<L>>, Any?> = CacheBuilder.newBuilder()
            .maximumSize(cacheSize.toLong())
            .removalListener<Pair<DataKey<*>, LocationWrapper<L>>, Any?> {
                underlying.set(it.key.first as DataKey<Any>, it.key.second.location, it.value)
            }.build(object : CacheLoader<Pair<DataKey<*>, LocationWrapper<L>>, Any?>() {
                override fun load(key: Pair<DataKey<*>, LocationWrapper<L>>): Any? {
                    return underlying.get(key.first, key.second.location)
                }
            })

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        val key = dataKey to LocationWrapper(locationKey, cacheKeyMapper)
        val value = try {
            cache.get(key)
        } catch (e: ExecutionException) {
            null
        }
        return value?.let { dataKey.copy(it as T) }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        cache.put(dataKey to LocationWrapper(locationKey, cacheKeyMapper), value)
    }

    override fun close() {
        cache.invalidateAll()
        super.close()
    }

    protected class LocationWrapper<L>(
            val location: L,
            val key: Any?
    ) {
        constructor(location: L, keyMapper: (L) -> Any?) : this(location, keyMapper(location))

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as LocationWrapper<*>
            if (key != other.key) return false
            return true
        }

        override fun hashCode(): Int {
            return key?.hashCode() ?: 0
        }
    }
}
