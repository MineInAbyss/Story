package io.github.paul1365972.story.datastore

import io.github.paul1365972.story.key.DataKeyI

interface DataStore<in L> {

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
    fun get(dataKey: DataKeyI, locationKey: L): ByteArray?

    /**
     * Serializes the value and saves it. If the value is null the specified data is removed from the location.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param data Value to be stored, or null to delete
     */
    fun set(dataKey: DataKeyI, locationKey: L, data: ByteArray?)

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
