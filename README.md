# Metay
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

Next acquire a metay service instance via the JavaPlugin 
```kotlin
val service: MetayService = server.servicesManager.load(MetayService.class);
```

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

You can also define nice extension functions
```kotlin
var Location.magicData: MagicData?
    get() = blockStore.get(magicKey, this)
    set(value) = blockStore.set(magicKey, this, value)

var Location.magicDataS: MagicData
    get() = blockStore.get(magicKey, this) { MagicData(0, "lorem") }
    set(value) = blockStore.put(magicKey, this, value)


loc.magicDataS.a += 3 // Dont do this as this doesnt call set, which could be bad TODO
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
