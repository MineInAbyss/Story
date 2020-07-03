package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.ObjectDataStore

abstract class ObjectFilterDataStore<in L>(
        val underlying: ObjectDataStore<L>
) : ObjectDataStore<L> by underlying
