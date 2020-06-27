package io.github.paul1365972.story.tracking

import org.bukkit.entity.Entity
import org.bukkit.entity.Player

fun MutableCollection<Entity>.trackEntityLoad(predicate: (Entity) -> Boolean): TrackingTask {
    TrackingListener.entityLoadCallbacks[this] = { if (predicate(it)) add(it) }
    return TrackingTask { TrackingListener.entityLoadCallbacks.remove(this) }
}

fun MutableCollection<Entity>.trackEntityUnload(): TrackingTask {
    TrackingListener.entityUnloadCallbacks[this] = { remove(it) }
    return TrackingTask { TrackingListener.entityUnloadCallbacks.remove(this) }
}

fun MutableCollection<Player>.trackPlayerLoad(predicate: (Player) -> Boolean): TrackingTask {
    TrackingListener.playerLoadCallbacks[this] = { if (predicate(it)) add(it) }
    return TrackingTask { TrackingListener.playerLoadCallbacks.remove(this) }
}

fun MutableCollection<Player>.trackPlayerUnload(): TrackingTask {
    TrackingListener.playerUnloadCallbacks[this] = { remove(it) }
    return TrackingTask { TrackingListener.playerUnloadCallbacks.remove(this) }
}
