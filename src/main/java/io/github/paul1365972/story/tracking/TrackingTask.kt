package io.github.paul1365972.story.tracking

class TrackingTask internal constructor(
        private val id: Long
) {
    fun cancel() = EntityTracker.removeCallback(id)
}
