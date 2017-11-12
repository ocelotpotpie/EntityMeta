package nu.nerd.entitymeta;

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