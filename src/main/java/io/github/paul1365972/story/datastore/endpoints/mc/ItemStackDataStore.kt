package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKeyI
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemStackDataStore : DataStore<ItemStack> {

    override fun get(dataKey: DataKeyI, locationKey: ItemStack): ByteArray? {
        return locationKey.itemMeta?.run {
            persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]
        }
    }

    override fun set(dataKey: DataKeyI, locationKey: ItemStack, data: ByteArray?) {
        locationKey.itemMeta?.apply {
            if (data != null)
                persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = data
            else
                persistentDataContainer.remove(dataKey.namespacedKey)
            locationKey.itemMeta = this
        }
    }
}
