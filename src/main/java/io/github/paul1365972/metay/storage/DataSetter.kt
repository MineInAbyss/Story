package io.github.paul1365972.metay.storage

class DataSetter<T>(
        private var data: T
) {
    private var changed = false
    internal fun get(): T = data
    fun set(value: T) {
        data = value
        changed = true
    }

    fun hasChanged() = changed
}
