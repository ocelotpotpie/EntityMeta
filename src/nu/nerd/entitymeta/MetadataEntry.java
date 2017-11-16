package nu.nerd.entitymeta;

import org.bukkit.ChatColor;

// ----------------------------------------------------------------------------
/**
 * Stores the Object value corresponding to a tag together with its serialised
 * representation as a String in order to facilitate efficient removal of the
 * tag from an {@link org.bukkit.entity.Entity}.
 * <p>
 * 
 * This class also facilitates iteration of all metadata attached to an entity
 * in the {@link EntityMetaAPI#getAllEntries(org.bukkit.entity.Entity)} and
 * {@link EntityMetaAPI#getPluginEntries(org.bukkit.entity.Entity, String)}
 * methods.
 */
public final class MetadataEntry {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param value the deserialised value.
     * @param type its type.
     * @param tag the serialised form of the value as a scoreboard tag,
     *        including the key prefix and type code.
     */
    public MetadataEntry(Object value, MetadataType type, String tag) {
        _value = value;
        _type = type;
        _tag = tag;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the deserialised value.
     * 
     * @return the deserialised value.
     */
    public Object getValue() {
        return _value;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the type of the metadata value.
     * 
     * @return the type of the metadata value.
     */
    public MetadataType getType() {
        return _type;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the serialised form of the value as a scoreboard tag, including
     * the key prefix and type code.
     * 
     * @return the serialised form of the value as a scoreboard tag, including
     *         the key prefix and type code.
     */
    public String getTag() {
        return _tag;
    }

    // ------------------------------------------------------------------------
    /**
     * Format this entry for presentation to the user.
     * 
     * Bukkit API colour codes are included to highlight the various parts of
     * the entry.
     * 
     * @param raw if true, the raw tag is returned; otherwise the type code and
     *        the value (converted to a String with {@link String#toString()}
     *        are returned.
     * @return the formatted entry.
     */
    public String format(boolean raw) {
        String[] tagParts = getTag().split(":", 3);
        String key = tagParts[0];
        String[] keyParts = key.split("\\.", 2);
        String pluginName = keyParts[0];
        String name = keyParts[1];

        if (raw) {
            return ChatColor.YELLOW + pluginName + ChatColor.WHITE + '.' + ChatColor.YELLOW + name +
                   ChatColor.GOLD + ':' + ChatColor.YELLOW + tagParts[1] +
                   ChatColor.GOLD + ':' + ChatColor.YELLOW + tagParts[2];
        } else {
            return ChatColor.YELLOW + pluginName + ChatColor.WHITE + '.' + ChatColor.YELLOW + name +
                   ChatColor.WHITE + " (" + ChatColor.GOLD + getType().getCode() +
                   ChatColor.WHITE + ")" + ChatColor.GOLD + " -> " +
                   ChatColor.YELLOW + getValue();
        }
    }

    // ------------------------------------------------------------------------
    /**
     * The deserialised value.
     */
    private final Object _value;

    /**
     * The type of the metadata value.
     */
    private final MetadataType _type;

    /**
     * The serialised form of the value as a scoreboard tag, including the key
     * prefix and type code.
     */
    private final String _tag;
} // class MetadataEntry