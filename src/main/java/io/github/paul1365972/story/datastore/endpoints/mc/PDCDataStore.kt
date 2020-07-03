package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKeyI
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

class PDCDataStore : DataStore<PersistentDataHolder> {

    override fun get(dataKey: DataKeyI, locationKey: PersistentDataHolder): ByteArray? {
        return locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]
    }

    override fun set(dataKey: DataKeyI, locationKey: PersistentDataHolder, data: ByteArray?) {
        if (data != null)
            locationKey.persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = data
        else
            locationKey.persistentDataContainer.remove(dataKey.namespacedKey)
    }
}
