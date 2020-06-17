package io.github.paul1365972.story.access

import io.github.paul1365972.story.datastore.DataSetter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface DataAccessI<T : Any, in L> {

    /**
     * Fetches the requested data and deserializes it.
     *
     * Note: Do not modify the returned value without calling [set] afterwards, as this can cause
     * data synchronization problems. Alternatively use [compute] or [update].
     *
     * @param locationKey Location of the value
     *
     * @return The deserialized value (that may be null) or null if the value is absent
     */
    fun get(locationKey: L): T?

    /**
     * @see [get]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun get(locationKey: L, defaultValue: () -> T): T {
        return get(locationKey) ?: defaultValue()
    }

    /**
     * Serializes the value and saves it. If the value is null the specified data is removed from the location.
     *
     * @param locationKey Location of the value
     * @param value Value to be stored, or null to delete
     */
    fun set(locationKey: L, value: T?)

    /**
     * Computes a new value at the specified location.
     *
     * If you only want to conditionally update the value use [update].
     *
     * @param locationKey Location of the value
     * @param block Function to comsete the new value, null removes the value
     *
     * @return The computed value
     */
    fun compute(locationKey: L, block: (T?) -> T?): T? {
        val modified = block(get(locationKey))
        set(locationKey, modified)
        return modified
    }

    /**
     * @see [compute]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun compute(locationKey: L, defaultValue: () -> T, block: (T) -> T): T {
        val modified = block(get(locationKey) ?: defaultValue())
        set(locationKey, modified)
        return modified
    }

    /**
     * Updates the value at the specified location.
     * In contrast to to [compute] the value is only stored if [DataSetter.set] is called.
     *
     * @param locationKey Location of the value
     * @param block Function to potentially update the value, null removes the value
     *
     * @return The updated value
     */
    fun update(locationKey: L, block: DataSetter<T?>.(T?) -> Unit): T? {
        val modified = DataSetter(get(locationKey))
        block(modified, modified._get())
        if (modified.hasChanged())
            set(locationKey, modified._get())
        return modified._get()
    }

    /**
     * @see [update]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun update(locationKey: L, defaultValue: () -> T, block: DataSetter<T>.(T) -> Unit): T {
        val modified = DataSetter(get(locationKey) ?: defaultValue())
        block(modified, modified._get())
        if (modified.hasChanged())
            set(locationKey, modified._get())
        return modified._get()
    }

    /**
     * Modifies and saves the value
     *
     * @param locationKey Location of the value
     * @param block Function to modify the value
     *
     * @return The modified value
     */
    fun modify(locationKey: L, block: T.() -> Unit): T? {
        return get(locationKey)?.apply {
            block(this)
            set(locationKey, this)
        }
    }

    /**
     * @see [modify]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun modify(locationKey: L, defaultValue: () -> T, block: T.() -> Unit): T {
        return (get(locationKey) ?: defaultValue()).apply {
            block(this)
            set(locationKey, this)
        }
    }

    /**
     * Provides a delegate to the value.
     * Note: Be careful as the value is only [set] when assigning the value, only modifying its properties does not
     *
     * @param locationKey Location of the value
     *
     * @return The value delegate
     */
    fun access(locationKey: L): ReadWriteProperty<Any?, T?> {
        return object : ReadWriteProperty<Any?, T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
                return get(locationKey)
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                set(locationKey, value)
            }
        }
    }

}
