package io.github.paul1365972.story.access

import io.github.paul1365972.story.StoryService
import io.github.paul1365972.story.datastore.PersistentDataStore
import io.github.paul1365972.story.key.PersistentDataKey
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

fun <T : Any> ItemStack.access(
        dataKey: PersistentDataKey<T>,
        dataStore: PersistentDataStore<ItemStack> = StoryService.defaultItemStore
) = InstanceAccess(dataStore, dataKey, this)

fun <T : Any> Entity.access(
        dataKey: PersistentDataKey<T>,
        dataStore: PersistentDataStore<Entity> = StoryService.defaultEntityStore
) = InstanceAccess(dataStore, dataKey, this)
