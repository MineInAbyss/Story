package io.github.paul1365972.story.tracking

class TrackingTask(
        private val callback: () -> Unit
) {
    fun untrack() = callback()
}
