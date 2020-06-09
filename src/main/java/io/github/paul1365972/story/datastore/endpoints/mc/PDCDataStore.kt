package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.DataKey
import io.github.paul1365972.story.datastore.StoryDataStore
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

class PDCDataStore : StoryDataStore<PersistentDataHolder> {

    override fun <T : Any> get(dataKey: DataKey<T>, locationKey: PersistentDataHolder): T? {
        return locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]?.let {
            dataKey.deserializer(it)
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T>, locationKey: PersistentDataHolder, value: T?) {
        if (value != null)
            locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = dataKey.serializer(value)
        else
            locationKey.persistentDataContainer.remove(dataKey.namespacedKey)
    }
}
