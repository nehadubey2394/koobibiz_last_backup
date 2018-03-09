package views.pickerview;

/**
 * Created by dharmraj on 8/1/18.
 */

public class DateTimeException extends RuntimeException {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -1632418723876261839L;

    /**
     * Constructs a new date-time exception with the specified message.
     *
     * @param message the message to use for this exception, may be null
     */
    public DateTimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new date-time exception with the specified message and cause.
     *
     * @param message the message to use for this exception, may be null
     * @param cause   the cause of the exception, may be null
     */
    public DateTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
