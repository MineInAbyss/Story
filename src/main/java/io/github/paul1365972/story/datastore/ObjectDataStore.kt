package io.github.paul1365972.story.datastore

import io.github.paul1365972.story.key.DataKeyI
import io.github.paul1365972.story.serializer.StorySerializer

interface ObjectDataStore<in L> : DataStore<L> {

    /**
     * Fetches the requested data and deserializes it.
     *
     * Note: Do not modify the returned value without calling [set] afterwards
     *
     * @param dataKey Key for the value
     * @param locationKey Location of the value
     *
     * @return The deserialized value (that may be null) or null if the value is absent
     */
    fun <T : Any> get(dataKey: DataKeyI, serializer: StorySerializer<T>, locationKey: L): T?

    /**
     * Serializes the value and saves it. If the value is null the specified data is removed from the location.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param value Value to be stored, or null to delete
     */
    fun <T : Any> set(dataKey: DataKeyI, serializer: StorySerializer<T>, locationKey: L, value: T?)

}
