Commands
--------
 * `/entitymeta-reload` - Reload the plugin configuration.
 * `/entitymeta-list [<plugin>]` - List metadata attached to the next 
   right-clicked entity, optionally restricting it to that owned by a 
   specific plugin.
 * `/entitymeta-list-raw [<plugin>]` - List raw metadata attached to 
   the next right-clicked entity, optionally restricting it to that 
   owned by a specific plugin. Raw metadata is the serialised form 
   of metadata.
 * `/entitymeta-find <entity-types> <radius> <key> [<type> <value>]` 
   - Find entities of the specified types - either `*` for any type, 
   or a comma separated list with no spaces, e.g. `ZOMBIE,CREEPER,SKELETON` -
   within `<radius>` blocks of you, that have metadata with the specified 
   `<key>`, of the form `<plugin>.<name>`, optionally restricting results 
   to those with the specified metadata `<type>` and `<value>`, as 
   described in `/help entitymeta-set`.
 

Permissions
-----------
 * `entitymeta-console` (`op`) - Permission to use commands that require console access (`/entitymeta-reload` only).
 * `entitymeta-admin` (`op`) - Permission to use all other commands.

 
 Configuration
 -------------
 | Setting         | Default | Description                         |
 | --------------- | ------  | ----------------------------------- |
 | `debug.config`  | false   | If true, log configuration loading. |
 | `debug.expiry`  | false   | If true, log cache entry expiry.    |