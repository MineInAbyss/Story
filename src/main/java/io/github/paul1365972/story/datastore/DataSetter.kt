package io.github.paul1365972.story.datastore

class DataSetter<T>(
        private var data: T
) {
    private var changed = false
    fun _get(): T = data
    fun set(value: T) {
        data = value
        changed = true
    }

    fun hasChanged() = changed
}
