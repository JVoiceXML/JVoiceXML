package org.jvoicexml.srgs;

public class SrgsSisrParsingException extends Exception {
    private static final long serialVersionUID = 7623516472103382901L;

    /**
     * Constructs a new event with the object type as its detail message. The
     * cause is not initialized.
     */
    public SrgsSisrParsingException() {
    }

    /**
     * Constructs a new object with the specified detail message. The cause is
     * not initialized.
     *
     * @param message
     *            The detail message.
     */
    public SrgsSisrParsingException(final String message) {
        super(message);
    }

    /**
     * Constructs a new object with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * @param cause
     *            The cause.
     */
    public SrgsSisrParsingException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new object with the specified detail message and cause.
     *
     * @param message
     *            The detail message.
     * @param cause
     *            The cause.
     */
    public SrgsSisrParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
