package io.github.paul1365972.metay.storage.endpoints

import io.github.paul1365972.metay.storage.DataKey
import io.github.paul1365972.metay.storage.MetayDataStore
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

class PDCDataStore : MetayDataStore<PersistentDataHolder> {

    override fun <T> get(dataKey: DataKey<T>, locationKey: PersistentDataHolder): T? {
        return locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]?.let {
            dataKey.deserializer(it)
        }
    }

    override fun <T> put(dataKey: DataKey<T>, locationKey: PersistentDataHolder, value: T) {
        locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = dataKey.serializer(value)
    }

    override fun <T> remove(dataKey: DataKey<T>, locationKey: PersistentDataHolder) {
        locationKey.persistentDataContainer.remove(dataKey.namespacedKey)
    }
}
