# Metay
Basic Paper plugin for attaching custom data to Blocks, Chunks, Worlds, Items, Players and Entities

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
val data = service.entityStore.get(magicKey, entity)

service.entityStore.put(magicKey, entity, data)

service.entityStore.compute(magicKey, entity) {
    val data = it ?: MagicData(0, "lorem")
    data.a += 3
}

service.entityStore.update(magicKey, entity) {
    val value = it ?: MagicData(0, "lorem")
    value.a += 3
    set(value)
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
