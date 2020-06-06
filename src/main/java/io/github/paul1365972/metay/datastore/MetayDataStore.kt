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
    fun <T> get(dataKey: DataKey<T>, locationKey: L): T?

    /**
     * @see [get]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun <T> get(dataKey: DataKey<T>, locationKey: L, defaultValue: (L) -> T): T {
        return get(dataKey, locationKey) ?: defaultValue(locationKey)
    }

    /**
     * Serializes the value and saves it.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param value Value to be stored
     */
    fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T)

    /**
     * Removes the specified data from the location.
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     */
    fun <T> remove(dataKey: DataKey<T>, locationKey: L)

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
    fun <T> compute(dataKey: DataKey<T>, locationKey: L, block: (T?) -> T?): T? {
        val modified = block(get(dataKey, locationKey))
        set(dataKey, locationKey, modified)
        return modified
    }

    /**
     * @see [compute]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun <T> compute(dataKey: DataKey<T>, locationKey: L, defaultValue: (L) -> T, block: (T) -> T): T {
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
    fun <T> update(dataKey: DataKey<T>, locationKey: L, block: DataSetter<T?>.(T?) -> Unit): T? {
        val modified = DataSetter(get(dataKey, locationKey))
        block(modified, modified.get())
        if (modified.hasChanged())
            set(dataKey, locationKey, modified.get())
        return modified.get()
    }

    /**
     * @see [update]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun <T> update(dataKey: DataKey<T>, locationKey: L, defaultValue: (L) -> T, block: DataSetter<T>.(T) -> Unit): T {
        val modified = DataSetter(get(dataKey, locationKey) ?: defaultValue(locationKey))
        block(modified, modified.get())
        if (modified.hasChanged())
            set(dataKey, locationKey, modified.get())
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
    fun <T> access(dataKey: DataKey<T>, locationKey: L): ReadWriteProperty<Any?, T?> {
        return object : ReadWriteProperty<Any?, T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
                return get(dataKey, locationKey)
            }
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                set(dataKey, locationKey, value)
            }
        }
    }

    /**
     * Serializes the value and saves it, or removes it if value is null
     *
     * @param dataKey Key for the data
     * @param locationKey Location of the value
     * @param value Value to be stored or null to remove
     */
    fun <T> set(dataKey: DataKey<T>, locationKey: L, value: T?) {
        if (value != null) put(dataKey, locationKey, value) else remove(dataKey, locationKey)
    }

    /**
     * Closes this data store and frees all resources.
     * This function may only be called once
     */
    fun close() {}
}
