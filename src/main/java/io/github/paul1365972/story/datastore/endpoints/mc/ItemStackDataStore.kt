package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemStackDataStore : DataStore<ItemStack> {

    override fun <T : Any> get(dataKey: DataKey<T, *>, locationKey: ItemStack): T? {
        return locationKey.itemMeta?.run {
            persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]
        }?.let {
            dataKey.deserialize(it)
        }
    }

    override fun <T : Any> set(dataKey: DataKey<T, *>, locationKey: ItemStack, value: T?) {
        locationKey.itemMeta?.apply {
            if (value != null)
                persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = dataKey.serialize(value)
            else
                persistentDataContainer.remove(dataKey.namespacedKey)
            locationKey.itemMeta = this
        }
    }
}
