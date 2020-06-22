package io.github.paul1365972.story.access

import io.github.paul1365972.story.datastore.StoryDataStore
import io.github.paul1365972.story.key.DataKey
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

fun <T : Any> ItemStack.access(dataKey: DataKey<T>, dataStore: StoryDataStore<ItemStack>): InstanceAccess<T, ItemStack> {
    return InstanceAccess(dataStore, dataKey, this)
}

fun <T : Any> Entity.access(dataKey: DataKey<T>, dataStore: StoryDataStore<Entity>): InstanceAccess<T, Entity> {
    return InstanceAccess(dataStore, dataKey, this)
}
