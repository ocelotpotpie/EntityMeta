package nu.nerd.entitymeta;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

// ----------------------------------------------------------------------------
/**
 * Manages the mapping from classes and type codes to {@link MetadataType}
 * instances.
 */
public class MetadataTypeManager {
    /**
     * The single instance of this class.
     */
    public static final MetadataTypeManager INSTANCE = new MetadataTypeManager();

    // ------------------------------------------------------------------------
    /**
     * Return the {@link MetadataType} appropriate for serialising the specified
     * value.
     * 
     * @param value the value to be serialised; must be non-null.
     * @return the {@link MetadataType} appropriate for serialising the
     *         specified value.
     * @throws EntityMetadataException if the value is of an unsupported type.
     */
    public MetadataType getType(Object value) {
        return getTypeByClass(value.getClass());
    }

    // ------------------------------------------------------------------------
    /**
     * Return the {@link MetadataType} appropriate for serialising values of the
     * specified class.
     * 
     * @param clazz the class of values to be serialised.
     * @return the {@link MetadataType} appropriate for serialising a value of
     *         that class.
     * @throws EntityMetadataException if the class is an unsupported type.
     */
    public MetadataType getTypeByClass(Class<?> clazz) {
        while (clazz != Object.class) {
            MetadataType format = _classToType.get(clazz);
            if (format != null) {
                return format;
            }

            for (Class<?> ifClazz : clazz.getInterfaces()) {
                format = _classToType.get(ifClazz);
                if (format != null) {
                    return format;
                }
            }

            clazz = clazz.getSuperclass();
        }
        throw new EntityMetadataException("type not supported");
    }

    // ------------------------------------------------------------------------
    /**
     * Return the {@link MetadataType} corresponding to the single character
     * type code, or null if not found.
     * 
     * @param code the type code.
     * @return the {@link MetadataType} corresponding to the single character
     *         type code, or null if not found.
     */
    public MetadataType getTypeByCode(char code) {
        return _codeToType.get(code);
    }

    // ------------------------------------------------------------------------
    /**
     * Return a collection of all supported metadata types.
     * 
     * @return a collection of all supported metadata types.
     */
    public Collection<MetadataType> getAllTypes() {
        return _codeToType.values();
    }

    // ------------------------------------------------------------------------
    /**
     * Add a handler for a new type.
     * 
     * @param type handles serialisation and deserialisation of values of the
     *        Java class returned by {@link MetadataType#getValueClass()}.
     */
    public void addType(MetadataType type) {
        _classToType.put(type.getValueClass(), type);
        _codeToType.put(type.getCode(), type);
    }

    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * Registers built-in type handlers.
     */
    private MetadataTypeManager() {
        // These type codes differ from the JVM spec type encoding for the sake
        // of better mnemonic value.
        addType(new MetadataType('b', Boolean.class) {
            @Override
            public Object fromString(String value) {
                return Boolean.valueOf(value);
            }
        });
        addType(new MetadataType('B', Byte.class) {
            @Override
            public Object fromString(String value) {
                return Byte.valueOf(value);
            }
        });
        addType(new MetadataType('c', Character.class) {
            @Override
            public Object fromString(String value) {
                return value.charAt(0);
            }
        });
        addType(new MetadataType('s', Short.class) {
            @Override
            public Object fromString(String value) {
                return Short.valueOf(value);
            }
        });
        addType(new MetadataType('i', Integer.class) {
            @Override
            public Object fromString(String value) {
                return Integer.valueOf(value);
            }
        });
        addType(new MetadataType('l', Long.class) {
            @Override
            public Object fromString(String value) {
                return Long.valueOf(value);
            }
        });
        addType(new MetadataType('f', Float.class) {
            @Override
            public Object fromString(String value) {
                return Float.valueOf(value);
            }
        });
        addType(new MetadataType('d', Double.class) {
            @Override
            public Object fromString(String value) {
                return Double.valueOf(value);
            }
        });
        addType(new MetadataType('S', String.class) {
            @Override
            public String toString(Object value) {
                return (String) value;
            }

            @Override
            public Object fromString(String value) {
                return value;
            }
        });
        addType(new MetadataType('u', UUID.class) {
            @Override
            public Object fromString(String value) {
                return UUID.fromString(value);
            }
        });
        addType(new MetadataType('*', ConfigurationSerializable.class) {
            // Note: shared, not thread safe.
            YamlConfiguration _config = new YamlConfiguration();

            @Override
            public String toString(Object value) {
                _config.set("value", value);
                return _config.saveToString();
            }

            @Override
            public Object fromString(String value) {
                try {
                    _config.loadFromString(value);
                    return _config.get("value");
                } catch (InvalidConfigurationException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        });
    } // constructor

    // ------------------------------------------------------------------------
    /**
     * Map from single character type code to corresponding type.
     */
    private final LinkedHashMap<Character, MetadataType> _codeToType = new LinkedHashMap<>();

    /**
     * Map from Class of value to encode to corresponding type.
     */
    private final LinkedHashMap<Class<?>, MetadataType> _classToType = new LinkedHashMap<>();

} // class MetadataTypeManager