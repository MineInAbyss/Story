package io.github.paul1365972.metay;

import io.github.paul1365972.metay.listener.BlockListener;
import io.github.paul1365972.metay.listener.LoadingListener;
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
		//TODO Add CacheDataStore to pipeline
		//TODO Estimate good cache sizes
		blockStore = new FileChunkedDataStore<>(new File(getDataFolder(), "block"), 1024,
				MCDataStoreUtil::toChunkKey, MCDataStoreUtil::toBlockKey);
		chunkStore = new FileChunkedDataStore<>(new File(getDataFolder(), "chunk"), 1024,
				MCDataStoreUtil::toSuperChunkKey, MCDataStoreUtil::toChunkKey);
		worldStore = new FolderDataStore<>(new File(getDataFolder(), "world"), world -> world.getUID().toString());
		entityStore = new TransformingDataStore<>(new PDCDataStore(), entity -> entity);
		itemStore = new TransformingDataStore<>(new PDCDataStore(), ItemStack::getItemMeta);
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
