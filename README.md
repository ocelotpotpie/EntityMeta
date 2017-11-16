EntityMeta
==========
Provides a persistent, type-safe metadata API for Bukkit entities.

Commands, Permissions, Configuration
------------------------------------
See [this page](https://nerdnu.github.io/EntityMeta/plugin). 


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

Building
--------
```sh
git clone https://github.com/NerdNu/EntityMeta
cd EntityMeta
mvn
```

Referencing as a Dependency
---------------------------
**Option 1**: Put the JAR file in a subdirectory of your project, e.g. `lib/`:

```xml
<dependency>
	<groupId>nu.nerd</groupId>
	<artifactId>entitymeta</artifactId>
	<version>1.0.0</version>
	<scope>system</scope>
	<systemPath>${basedir}/lib/EntityMeta-1.0.0.jar</systemPath>
</dependency>
```

**Option 2**: Install in the local Maven repository:
```
mvn install:install-file -Dfile=target/EntityMeta-1.0.0.jar -DgroupId=nu.nerd \
    -DartifactId=entitymeta -Dversion=1.0.0 -Dpackaging=jar
```

and reference it from your `pom.xml` as follows:

```xml
<dependencies>
	<dependency>
		<groupId>nu.nerd</groupId>
		<artifactId>entitymeta</artifactId>
		<version>1.0.0</version>
	</dependency>
</dependencies>
```
