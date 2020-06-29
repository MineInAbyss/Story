package io.github.paul1365972.story.datastore

import io.github.paul1365972.story.key.DataKey

interface DataStore<in L> {

    /**
     * Fetches the requested value
     *
     * @param dataKey Key for the value
     * @param locationKey Location of the value
     *
     * @return The deserialized value (that may be null) or null if the value is absent
     */
    fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T?

    /**
     * Saves the value. If the value is null the specified data is removed from the location.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param value Value to be stored, or null to delete
     */
    fun <T : Any> set(dataKey: DataKey<T>, locationKey: L, value: T?)

    /**
     * Flushes this data store and writes all caches.
     */
    fun flush() {}

    /**
     * Called once every tick
     */
    fun tick() {}

    /**
     * Closes this data store and frees all resources.
     * This function may only be called once
     */
    fun close() {}
}
