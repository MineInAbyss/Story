package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.ObjectDataStore
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
    val defaultBlockStore: ObjectDataStore<Location>

    /**
     * The public data store for accessing chunk level data
     */
    val defaultChunkStore: ObjectDataStore<Chunk>

    /**
     * The public data store for accessing world level data
     */
    val defaultWorldStore: ObjectDataStore<World>

    /**
     * The public data store for accessing tile entity level data
     */
    val defaultTileEntityStore: ObjectDataStore<BlockState>

    /**
     * The public data store for accessing entity level data
     */
    val defaultEntityStore: ObjectDataStore<Entity>

    /**
     * The public data store for accessing item level data
     */
    val defaultItemStore: ObjectDataStore<ItemStack>
}
