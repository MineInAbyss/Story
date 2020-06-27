package io.github.paul1365972.story.tracking

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent

object TrackingListener : Listener {

    val entityLoadCallbacks = mutableMapOf<Any, (Entity) -> Unit>()
    val entityUnloadCallbacks = mutableMapOf<Any, (Entity) -> Unit>()

    val playerLoadCallbacks = mutableMapOf<Any, (Player) -> Unit>()
    val playerUnloadCallbacks = mutableMapOf<Any, (Player) -> Unit>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onWorldLoad(event: WorldLoadEvent) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onWorldUnload(event: WorldUnloadEvent) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChunkLoad(event: ChunkLoadEvent) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChunkUnload(event: ChunkUnloadEvent) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityLoad(event: EntityAddToWorldEvent) {
        entityLoadCallbacks.forEach { it.value(event.entity) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityUnload(event: EntityRemoveFromWorldEvent) {
        entityUnloadCallbacks.forEach { it.value(event.entity) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        playerLoadCallbacks.forEach { it.value(event.player) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityLeave(event: PlayerQuitEvent) {
        playerUnloadCallbacks.forEach { it.value(event.player) }
    }
}
