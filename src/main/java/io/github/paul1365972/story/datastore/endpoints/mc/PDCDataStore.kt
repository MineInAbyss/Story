package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

class PDCDataStore : PersistentDataStore<PersistentDataHolder> {

    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: PersistentDataHolder): T? {
        return locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]?.let {
            dataKey.deserialize(it)
        }
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: PersistentDataHolder, value: T?) {
        if (value != null)
            locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = dataKey.serialize(value)
        else
            locationKey.persistentDataContainer.remove(dataKey.namespacedKey)
    }
}
