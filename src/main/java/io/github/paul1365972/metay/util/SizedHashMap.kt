package io.github.paul1365972.metay.util

import java.util.*

/**
 * Utility class for creating basic caches
 *
 * @property maxSize Maximum number of entries, or -1 for infinite
 * @property overflowListener Invoked every time the map drops the oldest entry. Modifying the map inside this callback is unspecified behaviour
 * @constructor Creates a new empty modifiable, sized and linked hashmap
 */
class SizedHashMap<K, V>(
        val maxSize: Int,
        val overflowListener: (K, V) -> Unit
) : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
    override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean {
        if (maxSize != -1 && size > maxSize) {
            overflowListener(eldest.key, eldest.value)
            return true
        }
        return false
    }
}
