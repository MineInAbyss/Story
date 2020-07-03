package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore
import io.github.paul1365972.story.key.DataKeyI

class DebugDataStore<L>(
        underlying: DataStore<L>
) : FilterDataStore<L>(underlying) {

    override fun get(dataKey: DataKeyI, locationKey: L): ByteArray? {
        println("Fetching ${dataKey.name} at $locationKey")
        return underlying.get(dataKey, locationKey)
    }

    override fun set(dataKey: DataKeyI, locationKey: L, data: ByteArray?) {
        println("Putting ${dataKey.name} with data $data at $locationKey")
        underlying.set(dataKey, locationKey, data)
    }
}
