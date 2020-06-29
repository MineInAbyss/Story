package io.github.paul1365972.story.tracking

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object TrackingListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityLoad(event: EntityAddToWorldEvent) {
        EntityTracker.entityLoadCallbacks.forEach { it.value(event.entity) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityUnload(event: EntityRemoveFromWorldEvent) {
        EntityTracker.entityUnloadCallbacks.forEach { it.value(event.entity) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        EntityTracker.playerLoadCallbacks.forEach { it.value(event.player) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityLeave(event: PlayerQuitEvent) {
        EntityTracker.playerUnloadCallbacks.forEach { it.value(event.player) }
    }
}
