package com.dataliquid.commons.xml.exception;

/**
 * The XpathException class represents an exception that is thrown for errors encountered during XPath processing.
 */
public class XpathException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new XpathException with a specific detail message.
     *
     * @param message the detail message
     */
    public XpathException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new XpathException with a specific detail message and a cause.
     *
     * @param message the detail message
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public XpathException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
