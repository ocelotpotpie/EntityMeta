Commands
--------
 * `/entitymeta-reload` - Reload the plugin configuration.
 * `/entitymeta-list [<plugin>] - List metadata attached to the next 
   right-clicked entity, optionally restricting it to that owned by a 
   specific plugin. 


Permissions
-----------
 * `entitymeta-console` (`op`) - Permission to use commands that require console access (`/entitymeta-reload` only).
 * `entitymeta-admin` (`op`) - Permission to use all other commands.

 
 Configuration
 -------------
 | Setting         | Default | Description                         |
 |:----------------|:--------|:------------------------------------|
 | `debug.config`  | false   | If true, log configuration loading. |
 | `debug.expiry`  | false   | If true, log cache entry expiry.    |