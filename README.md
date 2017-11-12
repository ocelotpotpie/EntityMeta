EntityMeta
==========
Provides a persistent, type-safe metadata API for entities.

API
---
See [the JavaDocs](https://nerdnu.github.io/EntityMeta/docs/api/index.html), particularly [class EntityMetaAPI](https://nerdnu.github.io/EntityMeta/docs/api/index.html?nu/nerd/entitymeta/EntityMetaAPI.html).

By Example
----------
Store a UUID, Location and double in an entity:
```java
Plugin myPlugin;
Entity entity;
Player owner;

EntityMeta.api().set(entity, myPlugin, "owner", owner.getUniqueId());
EntityMeta.api().set(entity, myPlugin, "location", owner.getLocation());
EntityMeta.api().set(entity, myPlugin, "chance", 1.2345);
```

Retrieve metadata:
```java
UUID ownerUuid = (UUID) EntityMeta.api().get(entity, myPlugin, "owner");
Location location = (Location) EntityMeta.api().get(entity, myPlugin, "location");
double chance = (Double) EntityMeta.api().get(entity, myPlugin, "chance");
```

Enumerate all metadata set by a specific plugin:
```java
String pluginName = "SomePlugin";
for (Entry<String, MetadataEntry> entry : EntityMeta.api().getPluginEntries(entity, pluginName)) {
    String key = entry.getKey(); // e.g. "SomePlugin.owner"
    MetadataEntry meta = entry.getValue();
    String className = meta.getType().getValueClass().getSimpleName();
    getLogger().info(key + " (" + className + ") = " + meta.getValue() + " (stored as: " + meta.getTag() + ")");
}
```
