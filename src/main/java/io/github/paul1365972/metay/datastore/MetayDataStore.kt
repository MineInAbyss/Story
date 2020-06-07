package io.github.paul1365972.metay.datastore

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface MetayDataStore<L> {

    /**
     * Fetches the requested data and deserializes it.
     *
     * Note: Do not modify the returned value without calling [put] afterwards, as this can cause
     * data synchronization problems. Alternatively use [compute] or [update].
     *
     * @param dataKey Key for the value
     * @param locationKey Location of the value
     *
     * @return The deserialized value (that may be null) or null if the value is absent
     */
    fun <T : Any> get(dataKey: DataKey<T>, locationKey: L): T?

    /**
     * @see [get]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun <T : Any> get(dataKey: DataKey<T>, locationKey: L, defaultValue: (L) -> T): T {
        return get(dataKey, locationKey) ?: defaultValue(locationKey)
    }

    /**
     * Serializes the value and saves it. If the value is null the specified data is removed from the location.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param value Value to be stored, or null to delete
     */
    fun <T : Any> put(dataKey: DataKey<T>, locationKey: L, value: T?)

    /**
     * Computes a new value at the specified location.
     *
     * If you only want to conditionally update the value use [update].
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param block Function to compute the new value, null removes the value
     *
     * @return The computed value
     */
    fun <T : Any> compute(dataKey: DataKey<T>, locationKey: L, block: (T?) -> T?): T? {
        val modified = block(get(dataKey, locationKey))
        put(dataKey, locationKey, modified)
        return modified
    }

    /**
     * @see [compute]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun <T : Any> compute(dataKey: DataKey<T>, locationKey: L, defaultValue: (L) -> T, block: (T) -> T): T {
        val modified = block(get(dataKey, locationKey) ?: defaultValue(locationKey))
        put(dataKey, locationKey, modified)
        return modified
    }

    /**
     * Updates the value at the specified location.
     * In contrast to to [compute] the value is only stored if [DataSetter.set] is called.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param block Function to potentially update the value, null removes the value
     *
     * @return The updated value
     */
    fun <T : Any> update(dataKey: DataKey<T>, locationKey: L, block: DataSetter<T?>.(T?) -> Unit): T? {
        val modified = DataSetter(get(dataKey, locationKey))
        block(modified, modified.get())
        if (modified.hasChanged())
            put(dataKey, locationKey, modified.get())
        return modified.get()
    }

    /**
     * @see [update]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun <T : Any> update(dataKey: DataKey<T>, locationKey: L, defaultValue: (L) -> T, block: DataSetter<T>.(T) -> Unit): T {
        val modified = DataSetter(get(dataKey, locationKey) ?: defaultValue(locationKey))
        block(modified, modified.get())
        if (modified.hasChanged())
            put(dataKey, locationKey, modified.get())
        return modified.get()
    }

    /**
     * Provides a delegate to the value.
     * Note: Be careful as the value is only [put] when assigning the value, only modifying its properties does not
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     *
     * @return The value delegate
     */
    fun <T : Any> access(dataKey: DataKey<T>, locationKey: L): ReadWriteProperty<Any?, T?> {
        return object : ReadWriteProperty<Any?, T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
                return get(dataKey, locationKey)
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                put(dataKey, locationKey, value)
            }
        }
    }

    /**
     * Closes this data store and frees all resources.
     * This function may only be called once
     */
    fun close() {}
}
