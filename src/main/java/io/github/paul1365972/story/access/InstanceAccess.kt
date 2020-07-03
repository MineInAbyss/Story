package io.github.paul1365972.story.access

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface InstanceAccess<T : Any, L> {

    /**
     * Fetches the requested data and deserializes it.
     *
     * Note: Do not modify the returned value without calling [set] afterwards, as this can cause
     * data synchronization problems. Alternatively use [compute] or [update].
     *
     * @return The deserialized value (that may be null) or null if the value is absent
     */
    fun get(): T?


    /**
     * Serializes the value and saves it. If the value is null the specified data is removed from the location.
     *
     * @param value Value to be stored, or null to delete
     */
    fun set(value: T?)

}

/**
 * @see [modify]
 * Uses the deserialized value or the default value, if the value is null or the data is missing
 */
inline fun <T : Any, L> InstanceAccess<T, L>.modify(defaultValue: () -> T, block: T.() -> Unit): T {
    return (get() ?: defaultValue()).apply {
        block(this)
        set(this)
    }
}

/**
 * Modifies and saves the value
 *
 * @param block Function to modify the value
 *
 * @return The modified value
 */
inline fun <T : Any, L> InstanceAccess<T, L>.modify(block: T.() -> Unit): T? {
    return get()?.apply {
        block(this)
        set(this)
    }
}

/**
 * @see [update]
 * Uses the deserialized value or the default value, if the value is null or the data is missing
 */
inline fun <T : Any, L> InstanceAccess<T, L>.update(defaultValue: () -> T, block: DataSetter<T>.(T) -> Unit): T {
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
 * Updates the value at the specified location.
 * In contrast to to [compute] the value is only stored if [DataSetter.set] is called.
 *
 * @param block Function to potentially update the value, null removes the value
 *
 * @return The updated value
 */
inline fun <T : Any, L> InstanceAccess<T, L>.update(block: DataSetter<T?>.(T?) -> Unit): T? {
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
 * @see [compute]
 * Uses the deserialized value or the default value, if the value is null or the data is missing
 */
inline fun <T : Any, L> InstanceAccess<T, L>.compute(defaultValue: () -> T, block: (T) -> T): T {
    val modified = block(get() ?: defaultValue())
    set(modified)
    return modified
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
inline fun <T : Any, L> InstanceAccess<T, L>.compute(block: (T?) -> T?): T? {
    val modified = block(get())
    set(modified)
    return modified
}

/**
 * @see [get]
 * Calls block with the deserialized value, if the value is null is does nothing
 */
inline fun <T : Any, L, R> InstanceAccess<T, L>.ifPresent(block: (T) -> R): R? {
    return get()?.let(block)
}

/**
 * @see [get]
 * Uses the deserialized value or the default value, if the value is null or the data is missing
 */
inline fun <T : Any, L> InstanceAccess<T, L>.get(defaultValue: () -> T): T {
    return get() ?: defaultValue()
}

/**
 * Provides a delegate to the value.
 * Note: Be careful as the value is only [set] when assigning the value, only modifying its properties does not
 *
 * @return The value delegate
 */
fun <T : Any, L> InstanceAccess<T, L>.access(): ReadWriteProperty<Any?, T?> {
    return object : ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            set(value)
        }
    }
}
