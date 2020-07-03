package io.github.paul1365972.story.datastore

import io.github.paul1365972.story.datastore.filters.FilterDataStore
import io.github.paul1365972.story.key.DataKeyI
import io.github.paul1365972.story.serializer.StorySerializer

open class SerializingDataStore<in L>(
        underlying: DataStore<L>
) : FilterDataStore<L>(underlying), ObjectDataStore<L> {
    override fun <T : Any> get(dataKey: DataKeyI, serializer: StorySerializer<T>, locationKey: L): T? {
        return underlying.get(dataKey, locationKey)?.let { serializer.deserialize(it) }
    }

    override fun <T : Any> set(dataKey: DataKeyI, serializer: StorySerializer<T>, locationKey: L, value: T?) {
        underlying.set(dataKey, locationKey, value?.let { serializer.serialize(it) })
    }
}
