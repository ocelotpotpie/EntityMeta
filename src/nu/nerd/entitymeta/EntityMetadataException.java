package nu.nerd.entitymeta;

// ----------------------------------------------------------------------------
/**
 * An unchecked exception thrown for any kind of error arising in the use of the
 * entity Metadata API.
 * 
 * It will typically wrap another exception that is the root cause.
 */
public class EntityMetadataException extends RuntimeException {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param message a message describing the reason for the exception.
     */
    public EntityMetadataException(String message) {
        super(message);
    }

    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param message a message describing the reason for the exception.
     * @param cause the exception that triggered this.
     */
    public EntityMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
} // class EntityMetadataException