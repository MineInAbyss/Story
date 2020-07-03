package io.github.paul1365972.story.access

import io.github.paul1365972.story.key.PersistentDataKeyI
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemStackInstanceAccess<T : Any>(
        val dataKey: PersistentDataKeyI<T>,
        val locationKey: ItemStack
) : InstanceAccess<T, ItemStack> {
    override fun get(): T? = locationKey.itemMeta?.persistentDataContainer
            ?.get(dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY)
            ?.let { dataKey.serializer.deserialize(it) }

    override fun set(value: T?) {
        value?.let {
            locationKey.itemMeta?.persistentDataContainer
                    ?.set(dataKey.namespacedKey, PersistentDataType.BYTE_ARRAY, dataKey.serializer.serialize(it))
        }
    }
}
