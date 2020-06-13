package io.github.paul1365972.story.datastore.wip

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.datastore.filters.FilterDataStore
import io.github.paul1365972.story.key.DataKey
import java.lang.ref.WeakReference

class ReferenceDataStore<L>(
        underlying: StoryDataStore<L>,
        val cacheKeyMapper: (L) -> Any? = { it }
) : FilterDataStore<L>(underlying) {

    @Suppress("UNCHECKED_CAST")
    private val references: Cache<Any, Entry<L>> = CacheBuilder.newBuilder()
            .weakKeys()
            .removalListener<Any, Entry<L>> {
                println("Lost reference to $it")
                val removed = accessCache.remove(it.value.dataKey to cacheKeyMapper(it.value.locationKey), it.key)
                if (removed)
                    underlying.set(it.value.dataKey as DataKey<Any>, it.value.locationKey, it.key)
            }.build()

    private val accessCache = mutableMapOf<Pair<DataKey<*>, Any?>, WeakReference<Any>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T? {
        references.cleanUp()
        val key = dataKey to cacheKeyMapper(locationKey)
        val weakValue = accessCache[key]
        val value = weakValue?.get()
        value?.let { return it as T }
        return underlying.get(dataKey, locationKey)?.apply {
            val old = accessCache.put(key, WeakReference(this))
            old?.get()?.let { references.invalidate(old) }
            if (references.getIfPresent(this) != null) throw RuntimeException("Two identical objects returned by get")
            references.put(this, Entry(dataKey, locationKey))
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        references.cleanUp()
        val key = dataKey to cacheKeyMapper(locationKey)
        if (value != null) {
            val old = accessCache.put(key, WeakReference(value))
            old?.get()?.let { references.invalidate(it) }
            if (references.getIfPresent(this) != null) throw RuntimeException("Two identical objects returned by get")
            references.put(value, Entry(dataKey, locationKey))
        } else {
            accessCache.remove(key)?.get()?.let { references.invalidate(it) }
            underlying.set(dataKey, locationKey, null)
        }
    }

    override fun close() {
        references.invalidateAll()
        super.close()
    }

    private data class Entry<T>(
            val dataKey: DataKey<*>,
            val locationKey: T
    )
}
