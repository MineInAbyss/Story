# Metay
Basic Paper plugin for attaching custom data to Blocks, Chunks, Worlds, Items, Players and Entities

**Warning:** This plugin is in an early development stage and has been tested exactly **0** times, use with care

## Example

You want to attatch some data to e.g. an entity.

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
val service: MetayService = getServer().getServicesManager().load(MetayService.class);
```

Now with an entity and the data key you can attach, get, modify and update the data
```kotlin
val data = service.entityStore.get(magicKey, entity)

service.entityStore.put(magicKey, entity, data)

service.entityStore.compute(magicKey, entity) {
    (it ?: MagicData(0, "lorem")).apply {
        a += 3
    }
}
```
