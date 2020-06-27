package io.github.paul1365972.story.access

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKey
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class InstanceAccess<T : Any, L>(
        val dataStore: DataStore<L>,
        val dataKey: DataKey<T>,
        val locationKey: L
) {

    /**
     * Fetches the requested data and deserializes it.
     *
     * Note: Do not modify the returned value without calling [set] afterwards, as this can cause
     * data synchronization problems. Alternatively use [compute] or [update].
     *
     * @return The deserialized value (that may be null) or null if the value is absent
     */
    fun get(): T? = dataStore.get(dataKey, locationKey)


    /**
     * Serializes the value and saves it. If the value is null the specified data is removed from the location.
     *
     * @param value Value to be stored, or null to delete
     */
    fun set(value: T?) = dataStore.set(dataKey, locationKey, value)


    /**
     * @see [get]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    inline fun get(defaultValue: () -> T): T {
        return get() ?: defaultValue()
    }

    /**
     * @see [get]
     * Calls block with the deserialized value, if the value is null is does nothing
     */
    inline fun <R> ifPresent(block: (T) -> R): R? {
        return get()?.let(block)
    }

    /**
     * Computes a new value at the specified location.
     *
     * If you only want to conditionally update the value use [update].
     *
     * @param block Function to comsete the new value, null removes the value
     *
     * @return The computed value
     */
    inline fun compute(block: (T?) -> T?): T? {
        val modified = block(get())
        set(modified)
        return modified
    }

    /**
     * @see [compute]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    inline fun compute(defaultValue: () -> T, block: (T) -> T): T {
        val modified = block(get() ?: defaultValue())
        set(modified)
        return modified
    }

    /**
     * Updates the value at the specified location.
     * In contrast to to [compute] the value is only stored if [DataSetter.set] is called.
     *
     * @param block Function to potentially update the value, null removes the value
     *
     * @return The updated value
     */
    inline fun update(block: DataSetter<T?>.(T?) -> Unit): T? {
        val modified = DataSetter(get())
        try {
            block(modified, modified._get())
            return modified._get()
        } finally {
            if (modified.hasChanged())
                set(modified._get())
        }
    }

    /**
     * @see [update]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    inline fun update(defaultValue: () -> T, block: DataSetter<T>.(T) -> Unit): T {
        val modified = DataSetter(get() ?: defaultValue())
        try {
            block(modified, modified._get())
            return modified._get()
        } finally {
            if (modified.hasChanged())
                set(modified._get())
        }
    }

    /**
     * Modifies and saves the value
     *
     * @param block Function to modify the value
     *
     * @return The modified value
     */
    inline fun modify(block: T.() -> Unit): T? {
        return get()?.apply {
            block(this)
            set(this)
        }
    }

    /**
     * @see [modify]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    inline fun modify(defaultValue: () -> T, block: T.() -> Unit): T {
        return (get() ?: defaultValue()).apply {
            block(this)
            set(this)
        }
    }

    /**
     * Provides a delegate to the value.
     * Note: Be careful as the value is only [set] when assigning the value, only modifying its properties does not
     *
     * @return The value delegate
     */
    fun access(): ReadWriteProperty<Any?, T?> {
        return object : ReadWriteProperty<Any?, T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
                return get()
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                set(value)
            }
        }
    }
}
