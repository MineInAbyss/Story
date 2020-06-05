package io.github.paul1365972.metay;

import io.github.paul1365972.metay.storage.MetayDataStore;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface MetayService {
	MetayDataStore<Location> getBlockStore();
	
	MetayDataStore<Chunk> getChunkStore();
	
	MetayDataStore<World> getWorldStore();
	
	MetayDataStore<Entity> getEntityStore();
	
	MetayDataStore<ItemStack> getItemStore();
	
}
