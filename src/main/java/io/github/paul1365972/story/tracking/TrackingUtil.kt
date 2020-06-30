package io.github.paul1365972.story.tracking

import org.bukkit.entity.Entity
import org.bukkit.entity.Player

fun MutableCollection<Entity>.trackEntityLoad(predicate: (Entity) -> Boolean) =
        TrackingTask(EntityTracker.addEntityLoadCallback({ if (predicate(it)) add(it) }))

fun MutableCollection<Entity>.trackEntityUnload() =
        TrackingTask(EntityTracker.addEntityUnloadCallback({ remove(it) }))

fun MutableCollection<Entity>.trackEntities(load: (Entity) -> Boolean, unload: (Entity) -> Unit) =
        TrackingTask(EntityTracker.addEntitiesCallback({ if (load(it)) add(it) }, { remove(it); unload(it) }))


fun MutableCollection<Entity>.trackPlayerLoad(predicate: (Player) -> Boolean) =
        TrackingTask(EntityTracker.addPlayerLoadCallback({ if (predicate(it)) add(it) }))

fun MutableCollection<Entity>.trackPlayerUnload() =
        TrackingTask(EntityTracker.addPlayerUnloadCallback({ remove(it) }))

fun MutableCollection<Entity>.trackPlayers(load: (Player) -> Boolean, unload: (Player) -> Unit) =
        TrackingTask(EntityTracker.addPlayersCallback({ if (load(it)) add(it) }, { remove(it); unload(it) }))
