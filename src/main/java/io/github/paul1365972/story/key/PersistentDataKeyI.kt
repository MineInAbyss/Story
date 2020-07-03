package io.github.paul1365972.story.key

import io.github.paul1365972.story.serializer.StorySerializer

interface PersistentDataKeyI<T : Any> : DataKeyI {
    val serializer: StorySerializer<T>
}
