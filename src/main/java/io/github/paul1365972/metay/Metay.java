package io.github.paul1365972.metay;

import io.github.paul1365972.metay.listener.BlockListener;
import io.github.paul1365972.metay.listener.LoadingListener;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Metay extends JavaPlugin implements MetayService, Listener {
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoadingListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getServicesManager().register(MetayService.class, this, this, ServicePriority.Normal);
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
