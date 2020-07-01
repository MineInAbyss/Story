package io.github.paul1365972.story

import io.github.paul1365972.story.datastore.DataStore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent

object DataStoreManager : Listener, Runnable {

    private val dataStores = mutableListOf<DataStore<*>>()

    fun register(dataStore: DataStore<*>) {
        dataStores += dataStore
    }

    override fun run() {
        dataStores.forEach { it.tick() }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPluginDisable(event: PluginDisableEvent) {
        dataStores.forEach { it.flush() }
    }

    fun close() {
        dataStores.forEach { it.close() }
    }

}

fun <T : DataStore<*>> T.register(): T = apply { DataStoreManager.register(this) }
