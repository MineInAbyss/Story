package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.PersistentDataStore
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockState
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

interface StoryService {

    companion object : @JvmStatic StoryService by Bukkit.getServer().servicesManager.load(StoryService::class.java)!!

    /**
     * The public data store for accessing block level data
     */
    val defaultBlockStore: PersistentDataStore<Location>

    /**
     * The public data store for accessing chunk level data
     */
    val defaultChunkStore: PersistentDataStore<Chunk>

    /**
     * The public data store for accessing world level data
     */
    val defaultWorldStore: PersistentDataStore<World>

    /**
     * The public data store for accessing tile entity level data
     */
    val defaultTileEntityStore: PersistentDataStore<BlockState>

    /**
     * The public data store for accessing entity level data
     */
    val defaultEntityStore: PersistentDataStore<Entity>

    /**
     * The public data store for accessing item level data
     */
    val defaultItemStore: PersistentDataStore<ItemStack>
}
