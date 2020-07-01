package io.github.paul1365972.story.access

import io.github.paul1365972.story.key.DataKey

operator fun <T : Any, L> L.get(
        dataKey: DataKey<T, L>
) = InstanceAccess(dataKey.defaultDataStore, dataKey, this)
