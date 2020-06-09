package io.github.paul1365972.metay

import io.github.paul1365972.metay.datastore.MetayDataStore
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

interface MetayService {

    companion object : @JvmStatic MetayService by Bukkit.getServer().servicesManager.load(MetayService::class.java)!!

    /**
     * The public data store for accessing block level data
     */
    val blockStore: MetayDataStore<Location>

    /**
     * The public data store for accessing chunk level data
     */
    val chunkStore: MetayDataStore<Chunk>

    /**
     * The public data store for accessing world level data
     */
    val worldStore: MetayDataStore<World>

    /**
     * The public data store for accessing tile entity level data
     */
    val tileEntityStore: MetayDataStore<Block>

    /**
     * The public data store for accessing entity level data
     */
    val entityStore: MetayDataStore<Entity>

    /**
     * The public data store for accessing item level data
     */
    val itemStore: MetayDataStore<ItemStack>
}
