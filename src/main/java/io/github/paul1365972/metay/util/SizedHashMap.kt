package io.github.paul1365972.metay.util

import java.util.*

class SizedHashMap<K, V>(
        val maxSize: Int,
        val overflowListener: (K, V) -> Unit
) : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
    override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean {
        if (maxSize < size) {
            overflowListener(eldest.key, eldest.value)
            return true
        }
        return false
    }
}
