package io.github.paul1365972.story.datastore.endpoints.mc

import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemStackDataStore : PersistentDataStore<ItemStack> {

    override fun <T : Any> get(dataKey: PersistentDataKey<T>, locationKey: ItemStack): T? {
        return locationKey.itemMeta?.run {
            persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY]
        }?.let {
            dataKey.deserialize(it)
        }
    }

    override fun <T : Any> set(dataKey: PersistentDataKey<T>, locationKey: ItemStack, value: T?) {
        locationKey.itemMeta?.apply {
            if (value != null)
                persistentDataContainer[dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY] = dataKey.serialize(value)
            else
                persistentDataContainer.remove(dataKey.namespacedKey)
            locationKey.itemMeta = this
        }
    }
}
