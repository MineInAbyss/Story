package io.github.paul1365972.metay.listener;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import io.github.paul1365972.metay.Metay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class BlockListener implements Listener {
	
	private final Metay metay;
	
	public BlockListener(Metay metay) {
		this.metay = metay;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
	}
	
	/*
	 * BlockEvent; // Like nearly every block event
	 * 		BlockPistonExtendEvent;
	 * 		BlockPistonRetractEvent;
	 * 		PortalCreateEvent;
	 * 		BlockExplodeEvent;
	 * 		BlockFromToEvent;
	 * 		AnvilDamagedEvent;
	 * 		BlockBreakEvent;
	 * 		TNTPrimeEvent; // Included in Block explode event i think
	 * 		EntityExplodeEvent;
	 * 		EntityChangeBlockEvent;
	 * 		EntityBreakDoorEvent; // Subtype of EntityChangeBlockEvent
	 * 		EntityInteractEvent; // Idk
	 * 		PlayerInteractEvent; // Idk
	 * 		PlayerBucketEvent; // And its two subclasses
	 */
	
}
