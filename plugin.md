Commands
--------
Note: almost all commands have a `help` option, e.g. `/entitymeta-list help`.
Equivalently, you can use the `/help <command>`, e.g. `/help entitymeta-list`.

 * `/entitymeta-reload` - Reload the plugin configuration.
 * `/entitymeta-list [<plugin>]` - List metadata attached to the next 
   right-clicked entity, optionally restricting it to that owned by a 
   specific plugin.
 * `/entitymeta-list-raw [<plugin>]` - List raw metadata attached to 
   the next right-clicked entity, optionally restricting it to that 
   owned by a specific plugin. Raw metadata is the serialised form 
   of metadata.
 * `/entitymeta-find <entity-types> <radius> <key> [<type> <value>]` -
   Find entities of the specified types - either `*` for any type, 
   or a comma separated list with no spaces, e.g. `ZOMBIE,CREEPER,SKELETON` -
   within `<radius>` blocks of you, that have metadata with the specified 
   `<key>`, of the form `<plugin>.<name>`, optionally restricting results 
   to those with the specified metadata `<type>` and `<value>`, as 
   described in `/help entitymeta-set`.
 * `/entitymeta-set <key> <type> <value>` - Set a metadata value
   on the next right-clicked entity under the specified `<key`, of
   the form `<plugin>.<name>`, with the specified `<type>` (given 
   as a single-letter code or Java class name) and having the 
   specified serialised value.
   * Note: `/entitymeta-set help` will list supported type codes and
     Java class names. 
 * `/entitymeta-set-test` - Set multiple test metadata values on 
   the next right-clicked entity.
 * `/entitymeta-get <key>` - Read metadata from the next right-clicked
   entity under the specified `<key>`, of the form `<plugin>.<name>`.   
 * `/entitymeta-get-raw <key>` - Read raw metadata from the next 
   right-clicked entity under the specified `<key>`, of the form 
   `<plugin>.<name>`.
 * `/entitymeta-clear <key>` - Clear a metadata value from the next 
   right-clicked entity under the specified `<key>`, of the form 
   `<plugin>.<name>`.


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