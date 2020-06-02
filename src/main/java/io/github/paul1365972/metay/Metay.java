package io.github.paul1365972.metay;

import io.github.paul1365972.metay.listener.BlockListener;
import io.github.paul1365972.metay.listener.LoadingListener;
import io.github.paul1365972.metay.storage.MetayDataStore;
import io.github.paul1365972.metay.storage.endpoints.MemoryDataStore;
import io.github.paul1365972.metay.storage.endpoints.PDCDataStore;
import io.github.paul1365972.metay.storage.impl.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Metay extends JavaPlugin implements MetayService, Listener {
	
	private LocationDataStore blockStore = null;
	private ChunkDataStore chunkStore = null;
	private UUIDDataStore worldStore = null;
	private EntityDataStore entityStore = null;
	private ItemDataStore itemStore = null;
	
	@Override
	public void onLoad() {
		blockStore = new LocationDataStore(new MemoryDataStore<>());
		chunkStore = new ChunkDataStore(new MemoryDataStore<>());
		worldStore = new UUIDDataStore(new MemoryDataStore<>());
		entityStore = new EntityDataStore(new PDCDataStore());
		itemStore = new ItemDataStore(new PDCDataStore());
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoadingListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getServicesManager().register(MetayService.class, this, this, ServicePriority.Normal);
	}
	
	@Override
	public void onDisable() {
		Arrays.asList(blockStore, chunkStore, worldStore, entityStore)
				.forEach(MetayDataStore::onClose);
	}
	
	@Override
	public LocationDataStore getBlockStore() {
		return blockStore;
	}
	
	@Override
	public ChunkDataStore getChunkStore() {
		return chunkStore;
	}
	
	@Override
	public UUIDDataStore getWorldStore() {
		return worldStore;
	}
	
	@Override
	public EntityDataStore getEntityStore() {
		return entityStore;
	}
	
	@Override
	public ItemDataStore getItemStore() {
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
