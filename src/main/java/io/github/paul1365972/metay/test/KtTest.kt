package io.github.paul1365972.metay.test

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.endpoints.MemoryDataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.bukkit.Location

val blockStore = MemoryDataStore<Location>()

@Serializable
data class MagicData(
        var a: Int,
        var b: String
)

val magicKey = DataKey(null, "magic", MagicData.serializer(), Json(JsonConfiguration.Stable))

fun test() {
    val loc = Location(null, 0.0, 0.0, 0.0)

    blockStore.compute(magicKey, loc) {
        (it ?: MagicData(0, "lorem")).apply {
            a += 3
        }
    }

    blockStore.update(magicKey, loc) {
        val value = it ?: MagicData(0, "lorem")
        value.a += 3
        set(value)
    }

    loc.magicData = (loc.magicData ?: MagicData(0, "lorem")).apply {
        a += 3
    }
    loc.magicData!!.a = 3
}

var Location.magicData: MagicData?
    get() = blockStore.get(magicKey, this)
    set(value) = blockStore.set(magicKey, this, value)
