package nu.nerd.entitymeta;

// ----------------------------------------------------------------------------
/**
 * Handles the serialisation and deserialisation of metadata values of a
 * specific Java class.
 * <p>
 * 
 * Note that the {@link #toString(Object)} and {@link #fromString(String)}
 * methods throw (unchecked) {@link java.lang.IllegalArgumentException}s on
 * error, which is a superclass of {@link java.lang.NumberFormatException}.
 * Checked Exceptions will be wrapped in
 * {@link java.lang.IllegalArgumentException}.
 */
public abstract class MetadataType {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param code the type code used to signify this encoding in serialised
     *        metadata.
     * @param clazz the Java class of the metadata to encode.
     */
    public MetadataType(char code, Class<?> clazz) {
        _code = code;
        _class = clazz;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the type code used to signify this encoding in serialised
     * metadata.
     * 
     * @return the type code used to signify this encoding in serialised
     *         metadata.
     */
    public char getCode() {
        return _code;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the class of the encoded metadata values.
     * 
     * @return the class of the encoded metadata values.
     */
    public Class<?> getValueClass() {
        return _class;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the string encoding of the specified metadata value.
     * 
     * @param value the value to encode.
     * @return the value encoded as a string.
     * 
     * @throws IllegalArgumentException in the event of failure to serialise
     *         (possible wrapping a checked exception).
     */
    public String toString(Object value) throws IllegalArgumentException {
        return value.toString();
    }

    // ------------------------------------------------------------------------
    /**
     * Return the value corresponding to the specified serialised metadata
     * string.
     * 
     * @param value the serialised form of the metadata.
     * @return the corresponding deserialised value.
     * 
     * @throws IllegalArgumentException in the event of failure to deserialise
     *         (possible wrapping a checked exception).
     */
    public abstract Object fromString(String value) throws IllegalArgumentException;

    // ------------------------------------------------------------------------
    /**
     * The type code used to signify this encoding in serialised metadata.
     */
    private final char _code;

    /**
     * The class of encoded metadata values.
     */
    private final Class<?> _class;
} // class MetadataType