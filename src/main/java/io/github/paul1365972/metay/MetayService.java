package io.github.paul1365972.metay;

import io.github.paul1365972.metay.datastore.MetayDataStore;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface MetayService {
	
	/**
	 * @return The public data store for accessing block level data
	 */
	MetayDataStore<Location> getBlockStore();
	
	/**
	 * @return The public data store for accessing chunk level data
	 */
	MetayDataStore<Chunk> getChunkStore();
	
	/**
	 * @return The public data store for accessing world level data
	 */
	MetayDataStore<World> getWorldStore();
	
	/**
	 * @return The public data store for accessing tile entity level data
	 */
	MetayDataStore<Block> getTileEntityStore();
	
	/**
	 * @return The public data store for accessing entity level data
	 */
	MetayDataStore<Entity> getEntityStore();
	
	/**
	 * @return The public data store for accessing item level data
	 */
	MetayDataStore<ItemStack> getItemStore();
	
}
