package io.github.paul1365972.story.datastore.caches

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalCause
import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.datastore.filters.FilterDataStore
import io.github.paul1365972.story.key.DataKey
import java.util.concurrent.ExecutionException

class CacheDataStore<L> @JvmOverloads constructor(
        underlying: DataStore<L>,
        val cacheSize: Int,
        val cacheKeyMapper: (L) -> Any? = { it },
        val copyFresh: Boolean = true,
        val writeThrough: Boolean = false
) : FilterDataStore<L>(underlying) {

    private val cache: LoadingCache<Pair<DataKey<*, *>, LocationWrapper<L>>, ValueWrapper<Any?>> = CacheBuilder.newBuilder()
            .maximumSize(cacheSize.toLong())
            .removalListener<Pair<DataKey<*, *>, LocationWrapper<L>>, ValueWrapper<Any?>> {
                if (it.cause != RemovalCause.REPLACED && it.value.dirty) {
                    @Suppress("UNCHECKED_CAST")
                    underlying.set(it.key.first as DataKey<Any, *>, it.key.second.location, it.value.value)
                }
            }.build(object : CacheLoader<Pair<DataKey<*, *>, LocationWrapper<L>>, ValueWrapper<Any?>>() {
                override fun load(key: Pair<DataKey<*, *>, LocationWrapper<L>>): ValueWrapper<Any?> {
                    return underlying.get(key.first, key.second.location)?.let { ValueWrapper(it) } ?: throw Exception()
                }
            })


    override fun <T : Any> get(dataKey: DataKey<T, *>, locationKey: L): T? {
        val key = dataKey to LocationWrapper(locationKey, cacheKeyMapper)
        val value = try {
            @Suppress("UNCHECKED_CAST")
            cache.get(key)?.value as T?
        } catch (e: ExecutionException) {
            null
        }
        return if (copyFresh) value?.let { dataKey.copy(it) } else value
    }


    override fun <T : Any> set(dataKey: DataKey<T, *>, locationKey: L, value: T?) {
        if (writeThrough)
            underlying.set(dataKey, locationKey, value)
        cache.put(dataKey to LocationWrapper(locationKey, cacheKeyMapper), ValueWrapper(value, !writeThrough))
    }

    override fun flush() {
        cache.invalidateAll()
        super.flush()
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

        override fun hashCode(): Int = key?.hashCode() ?: 0
    }

    protected data class ValueWrapper<out T>(
            val value: T,
            var dirty: Boolean = false
    )
}
