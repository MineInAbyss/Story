# Story
Basic Paper plugin for attaching custom data to:
- Worlds
- Chunks
- Blocks
- Tile Entities
- Entities (and Players)
- Items

**Warning:** This plugin is in an early development stage and has been tested exactly **0** times, use with care

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

val magicKey = DataKey(plugin, "magic", MagicData.serializer(), Json(JsonConfiguration.Stable))
```

Next acquire a story service instance via the JavaPlugin 
```kotlin
val service: StoryService = server.servicesManager.load(StoryService.class);
```

Or use e.g. `StoryService.entityStore` directly

Now with an entity and the data key you can attach, get, modify and update the data
```kotlin
val value = service.entityStore.get(magicKey, entity)

service.entityStore.put(magicKey, entity, value)

service.entityStore.compute(magicKey, entity) {
    val value = it ?: MagicData(0, "lorem")
    value.a += 3
}

service.entityStore.update(magicKey, entity) {
    val value = it ?: MagicData(0, "lorem")
    value.a += 3
    set(value)
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
itemStore = new CacheDataStore<>(
        new TransformingDataStore<>(
        new PDCDataStore(), ItemStack::getItemMeta),
        cacheSize = 4096)
```

Also remember to close the data store `itemStore.close()`
