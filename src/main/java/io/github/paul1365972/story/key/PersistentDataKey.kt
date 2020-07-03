package io.github.paul1365972.story.key

import io.github.paul1365972.story.serializer.StorySerializer
import org.bukkit.plugin.Plugin

open class PersistentDataKey<T : Any>(
        plugin: Plugin,
        name: String,
        override val serializer: StorySerializer<T>
) : DataKey(plugin, name), PersistentDataKeyI<T>
