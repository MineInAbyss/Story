package io.github.paul1365972.story.access

import io.github.paul1365972.story.datastore.DataSetter
import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class InstanceAccess<T : Any, L>(
        val dataStore: StoryDataStore<L>,
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
    fun get(defaultValue: () -> T): T {
        return get() ?: defaultValue()
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
    fun compute(block: (T?) -> T?): T? {
        val modified = block(get())
        set(modified)
        return modified
    }

    /**
     * @see [compute]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun compute(defaultValue: () -> T, block: (T) -> T): T {
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
    fun update(block: DataSetter<T?>.(T?) -> Unit): T? {
        val modified = DataSetter(get())
        block(modified, modified.get())
        if (modified.hasChanged())
            set(modified.get())
        return modified.get()
    }

    /**
     * @see [update]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun update(defaultValue: () -> T, block: DataSetter<T>.(T) -> Unit): T {
        val modified = DataSetter(get() ?: defaultValue())
        block(modified, modified.get())
        if (modified.hasChanged())
            set(modified.get())
        return modified.get()
    }

    /**
     * Modifies and saves the value
     *
     * @param block Function to modify the value
     *
     * @return The modified value
     */
    fun modify(block: T.() -> Unit): T? {
        return get()?.apply {
            block(this)
            set(this)
        }
    }

    /**
     * @see [modify]
     * Uses the deserialized value or the default value, if the value is null or the data is missing
     */
    fun modify(defaultValue: () -> T, block: T.() -> Unit): T {
        return (get() ?: defaultValue()).apply {
            block(this)
            set(this)
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
                return get()
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                set(value)
            }
        }
    }

}
