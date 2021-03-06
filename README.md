# Story

[![GitHub-CI Workflow Status](https://badgen.net/github/checks/MineInAbyss/Story?label=Github%20Build&icon=github&cache=3600)](https://github.com/Paul1365972/Story/actions?query=workflow%3A%22Build%22)
[![CodeMC-CI Build Status](https://badgen.net/runkit/jenkins-status-vbryjbp7mcuc/ci.codemc.io%2Fjob%2FMineInAbyss%2Fjob%2FStory?label=CodeMC%20Build&icon=https%3A%2F%2Fsvgshare.com%2Fi%2FKEK.svg&cache=3600)](https://ci.codemc.io/job/MineInAyss/job/Story/)

Basic Paper plugin for managing custom data and attaching it to: 
- Worlds
- Chunks
- Blocks
- Tile Entities
- Items
- Entities (and Players)

Supports managing entity groups, for higher performance entity lookups

**Warning:** This plugin is in an early development stage and has been tested exactly **0** times, everything should work, but use with care

Examples are mostly outdated

## Example

### Basic data management

You can attach some data to e.g. an entity.

Create you magic data class and instantiate a DataKey
```kotlin
@Serializable
data class MagicData(
    var a: Int,
    var b: String
)

val magicKey = DataKey(plugin, "magic", MagicData.serializer(), Json(JsonConfiguration.Stable), { it.copy() })
```

Next acquire a story service instance via the JavaPlugin 
```kotlin
val service: StoryService = server.servicesManager.load(StoryService.class)
```

Or use e.g. `StoryService.entityStore` directly

Now with an entity and the data key you can attach, get, modify and update the data
```kotlin
val value = service.entityStore.get(magicKey, entity)

service.entityStore.set(magicKey, entity, value)

service.entityStore.compute(magicKey, entity) {
    val value = it ?: MagicData(0, "lorem")
    value.a += 3
    value
}

service.entityStore.update(magicKey, entity) {
    val value = it ?: MagicData(0, "lorem")
    value.a += 3
    set(value)
}

service.entityStore.modify(magicKey, entity) {
    a += 3
}
```

You can also define a nice extension property, but do note that all calls to `loc.magicData` return a new object
```kotlin
var Location.magicData: MagicData?
    get() = blockStore.get(magicKey, this)
    set(value) = blockStore.set(magicKey, this, value)

loc.magicData = (loc.magicData ?: MagicData(0, "lorem")).apply {
    a += 3
}
```

### Custom data stores

You can also create custom data stores in case the predefined ones are lacking.

```kotlin
val entityStore: StoryDataStore<Entity> = 
        CacheDataStore<Entity>(PDCDataStore(), 
            4096, { it.uniqueId }
        )
```

Also remember to close the data store `onDisable()` with `itemStore.close()`
