package io.github.paul1365972.metay.test

import io.github.paul1365972.metay.datastore.DataKey
import io.github.paul1365972.metay.datastore.endpoints.MemoryDataStore
import io.github.paul1365972.metay.datastore.filters.DebugDataStore
import io.github.paul1365972.metay.datastore.wip.ReferenceDataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.bukkit.Location
import org.junit.Test

val blockStore = ReferenceDataStore(DebugDataStore(MemoryDataStore<Location>()))

@Serializable
data class MagicData(
        var a: Int,
        var b: String
)

val magicKey = DataKey(null, "magic", MagicData.serializer(), Json(JsonConfiguration.Stable))

var Location.magicData: MagicData?
    get() = blockStore.get(magicKey, this)
    set(value) = blockStore.set(magicKey, this, value)

class KtTest {

    @Test
    fun test1() {
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

    val loc = Location(null, 0.0, 0.0, 0.0)

    @Test
    fun test2() {
        println("Magic data: ${loc.magicData}")

        loc.magicData = MagicData(0, "lorem")
        loc.magicData!!.a += 3

        setMagic()

        System.gc()

        println("Magic data: ${loc.magicData}")
    }

    private fun setMagic() {
        val magicData = loc.magicData ?: MagicData(0, "lorem")
        magicData.a += 5
    }

    @Test
    fun test3() {
        var magicData by blockStore.access(magicKey, loc)
        magicData = MagicData(0, "lorem")
    }
}
