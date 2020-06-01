package io.github.paul1365972.metay.storage

import io.github.paul1365972.metay.database.MetayDatabase

class DatabaseDataStore<L>(
        val database: MetayDatabase
) : MetayDataStore<L> {

    override fun <T> get(dataKey: DataKey<T>, locationKey: L): T? {
        return database.get(toKey(dataKey, locationKey))?.let {
            dataKey.deserializer(it)
        }
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: L, value: T) {
        database.put(toKey(dataKey, locationKey), dataKey.serializer(value))
    }

    override fun onClose() = database.onClose()

}
