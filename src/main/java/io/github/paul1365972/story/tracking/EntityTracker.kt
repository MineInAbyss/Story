package io.github.paul1365972.story.tracking

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicLong

object EntityTracker {

    private val nextId = AtomicLong(0)

    internal val entityLoadCallbacks = mutableMapOf<Long, (Entity) -> Unit>()
    internal val entityUnloadCallbacks = mutableMapOf<Long, (Entity) -> Unit>()

    internal val playerLoadCallbacks = mutableMapOf<Long, (Player) -> Unit>()
    internal val playerUnloadCallbacks = mutableMapOf<Long, (Player) -> Unit>()

    fun addEntityLoadCallback(cb: (Entity) -> Unit, id: Long = nextId()) = id.also {
        entityLoadCallbacks[it] = cb
    }

    fun addEntityUnloadCallback(cb: (Entity) -> Unit, id: Long = nextId()) = id.also {
        entityUnloadCallbacks[it] = cb
    }

    fun addEntitiesCallback(load: (Entity) -> Unit, unload: (Entity) -> Unit, id: Long = nextId()) = id.also {
        entityLoadCallbacks[it] = load
        entityUnloadCallbacks[it] = unload
    }

    fun addPlayerLoadCallback(cb: (Player) -> Unit, id: Long = nextId()) = id.also {
        playerLoadCallbacks[it] = cb
    }

    fun addPlayerUnloadCallback(cb: (Player) -> Unit, id: Long = nextId()) = id.also {
        playerUnloadCallbacks[it] = cb
    }

    fun addPlayersCallback(load: (Player) -> Unit, unload: (Player) -> Unit, id: Long = nextId()) = id.also {
        playerLoadCallbacks[it] = load
        playerUnloadCallbacks[it] = unload
    }

    fun removeCallback(id: Long) {
        listOf(entityLoadCallbacks, entityUnloadCallbacks, playerLoadCallbacks, playerUnloadCallbacks).forEach {
            it.remove(id)
        }
    }

    fun nextId(): Long = nextId.getAndIncrement()
}
