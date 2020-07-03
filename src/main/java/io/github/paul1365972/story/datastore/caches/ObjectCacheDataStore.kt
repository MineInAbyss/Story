package io.github.paul1365972.story.datastore.caches

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalCause
import io.github.paul1365972.story.datastore.ObjectDataStore
import io.github.paul1365972.story.datastore.filters.ObjectFilterDataStore
import io.github.paul1365972.story.key.DataKeyI
import java.util.concurrent.ExecutionException

// TODO
class ObjectCacheDataStore<in L> @JvmOverloads constructor(
        underlying: ObjectDataStore<L>,
        val cacheSize: Int,
        val cacheKeyMapper: (L) -> Any? = { it },
        val writeThrough: Boolean = false
) : ObjectFilterDataStore<L>(underlying) {

    private val cache: LoadingCache<Pair<DataKeyI, LocationWrapper<L>>, DataWrapper> = CacheBuilder.newBuilder()
            .maximumSize(cacheSize.toLong())
            .removalListener<Pair<DataKeyI, LocationWrapper<L>>, DataWrapper> {
                if (it.cause != RemovalCause.REPLACED && it.value.dirty) {
                    @Suppress("UNCHECKED_CAST")
                    underlying.set(it.key.first, it.key.second.location, it.value.value)
                }
            }.build(object : CacheLoader<Pair<DataKeyI, LocationWrapper<L>>, DataWrapper>() {
                override fun load(key: Pair<DataKeyI, LocationWrapper<L>>): DataWrapper {
                    return underlying.get(key.first, key.second.location)?.let { DataWrapper(it) } ?: throw Exception()
                }
            })


    override fun get(dataKey: DataKeyI, locationKey: L): ByteArray? {
        val key = dataKey to LocationWrapper(locationKey, cacheKeyMapper)
        return try {
            @Suppress("UNCHECKED_CAST")
            cache.get(key)?.value
        } catch (e: ExecutionException) {
            null
        }
    }


    override fun set(dataKey: DataKeyI, locationKey: L, data: ByteArray?) {
        if (writeThrough)
            underlying.set(dataKey, locationKey, data)
        cache.put(dataKey to LocationWrapper(locationKey, cacheKeyMapper), DataWrapper(data, !writeThrough))
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

    protected data class DataWrapper(
            val value: ByteArray?,
            var dirty: Boolean = false
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DataWrapper

            if (value != null) {
                if (other.value == null) return false
                if (!value.contentEquals(other.value)) return false
            } else if (other.value != null) return false
            if (dirty != other.dirty) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value?.contentHashCode() ?: 0
            result = 31 * result + dirty.hashCode()
            return result
        }
    }
}
