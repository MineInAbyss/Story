package io.github.paul1365972.metay.storage

class DataModifier<T>(
        private var data: T
) {
    private var changed = false
    fun get(): T = data
    fun set(value: T) {
        data = value
        changed = true
    }

    fun hasChanged() = changed
}
