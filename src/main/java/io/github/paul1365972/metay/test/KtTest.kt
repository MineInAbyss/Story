package io.github.paul1365972.metay.test

import io.github.paul1365972.metay.storage.DataKey
import io.github.paul1365972.metay.storage.endpoints.MemoryDataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.bukkit.Location

//TODO
fun test() {
    val loc = Location(null, 0.0, 0.0, 0.0)

    val blockStore = MemoryDataStore<Location>()

    @Serializable
    data class MagicData(
            var a: Int,
            var b: String
    )

    val magicKey = DataKey(null, "magic", MagicData.serializer(), Json(JsonConfiguration.Stable))

    blockStore.update(magicKey, loc) {
        (it ?: MagicData(0, "lorem")).apply {
            a += 3
        }
    }

    blockStore.compute(magicKey, loc) {
        set((it ?: MagicData(0, "lorem")).apply {
            a += 3
        }
        )
    }
}
