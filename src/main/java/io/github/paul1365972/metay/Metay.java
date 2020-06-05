package io.github.paul1365972.metay;

import io.github.paul1365972.metay.listener.BlockListener;
import io.github.paul1365972.metay.listener.LoadingListener;
import io.github.paul1365972.metay.storage.CacheDataStore;
import io.github.paul1365972.metay.storage.MetayDataStore;
import io.github.paul1365972.metay.storage.TransformingDataStore;
import io.github.paul1365972.metay.storage.endpoints.FileChunkedDataStore;
import io.github.paul1365972.metay.storage.endpoints.FolderDataStore;
import io.github.paul1365972.metay.storage.endpoints.PDCDataStore;
import io.github.paul1365972.metay.util.MCDataStoreUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public final class Metay extends JavaPlugin implements MetayService, Listener {
	
	private MetayDataStore<Location> blockStore = null;
	private MetayDataStore<Chunk> chunkStore = null;
	private MetayDataStore<World> worldStore = null;
	private MetayDataStore<Entity> entityStore = null;
	private MetayDataStore<ItemStack> itemStore = null;
	
	@Override
	public void onLoad() {
		// TODO Estimate good cache sizes
		
		// Chunk cache size of 1024 seems reasonable if we expect each player to interact with 5 chunks at a time
		// Cache size just guesstimated, as i am still not sure how this will be used
		blockStore = new CacheDataStore<>(
				new FileChunkedDataStore<>(new File(getDataFolder(), "block"), 256,
						MCDataStoreUtil::toChunkKey, MCDataStoreUtil::toBlockKey),
				1024, MCDataStoreUtil::toBlockKey);
		// We load max 512 super chunks at a time, just guessing
		// 10x10 view distance at 50 plays leads to 5000 chunks, so we cache about double that amount
		chunkStore = new CacheDataStore<>(
				new FileChunkedDataStore<>(new File(getDataFolder(), "chunk"), 512,
						MCDataStoreUtil::toSuperChunkKey, MCDataStoreUtil::toChunkKey),
				8192, MCDataStoreUtil::toChunkKey);
		// We wont have more than 128 worlds
		worldStore = new CacheDataStore<>(
				new FolderDataStore<>(new File(getDataFolder(), "world"), world -> world.getUID().toString()),
				128, World::getUID);
		// Do we need to make the entity object the key ("identity" keys) or the uuid?
		// Cache size just guesstimated, 50 players with ~100 mobs each expected
		entityStore = new CacheDataStore<>(
				new TransformingDataStore<>(new PDCDataStore(), entity -> entity),
				8124, Entity::getUniqueId);
		// 4096 should be enough when we expect ~50 players with their inventories
		itemStore = new CacheDataStore<>(
				new TransformingDataStore<>(new PDCDataStore(), ItemStack::getItemMeta),
				4096);
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoadingListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getServicesManager().register(MetayService.class, this, this, ServicePriority.Normal);
	}
	
	@Override
	public void onDisable() {
		Arrays.asList(blockStore, chunkStore, worldStore, entityStore, itemStore)
				.forEach(MetayDataStore::onClose);
	}
	
	public MetayDataStore<Location> getBlockStore() {
		return blockStore;
	}
	
	public MetayDataStore<Chunk> getChunkStore() {
		return chunkStore;
	}
	
	public MetayDataStore<World> getWorldStore() {
		return worldStore;
	}
	
	public MetayDataStore<Entity> getEntityStore() {
		return entityStore;
	}
	
	public MetayDataStore<ItemStack> getItemStore() {
		return itemStore;
	}
	
	public void onWorldLoad(World world) {
	}
	
	public void onWorldUnload(World world) {
	}
	
	public void onChunkLoad(Chunk chunk) {
	}
	
	public void onChunkUnload(Chunk chunk) {
	}
	
	public void onEntityLoad(Entity entity) {
	}
	
	public void onEntityUnload(Entity entity) {
	}
	
	public void onPlayerLoad(Player player) {
	}
	
	public void onPlayerUnload(Player player) {
	}
	
}
