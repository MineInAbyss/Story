package io.github.paul1365972.story.key

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

interface DataKeyI {
    val plugin: Plugin
    val name: String
    val namespacedName: String
        get() = "${plugin.name}:$name"
    val namespacedKey: NamespacedKey
        get() = NamespacedKey(plugin, name)
}
