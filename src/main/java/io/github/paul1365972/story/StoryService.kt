package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.StoryDataStore
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
    val blockStore: StoryDataStore<Location>

    /**
     * The public data store for accessing chunk level data
     */
    val chunkStore: StoryDataStore<Chunk>

    /**
     * The public data store for accessing world level data
     */
    val worldStore: StoryDataStore<World>

    /**
     * The public data store for accessing tile entity level data
     */
    val tileEntityStore: StoryDataStore<BlockState>

    /**
     * The public data store for accessing entity level data
     */
    val entityStore: StoryDataStore<Entity>

    /**
     * The public data store for accessing item level data
     */
    val itemStore: StoryDataStore<ItemStack>
}
