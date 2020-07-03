package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.DataStore

abstract class FilterDataStore<in L>(
        val underlying: DataStore<L>
) : DataStore<L> by underlying
