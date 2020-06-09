package io.github.paul1365972.story.datastore.filters

import io.github.paul1365972.story.datastore.StoryDataStore

abstract class FilterDataStore<L>(
        val underlying: StoryDataStore<in L>
) : StoryDataStore<L> {
    override fun close() = underlying.close()
}
