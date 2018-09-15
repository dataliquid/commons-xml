package com.dataliquid.commons.xml.exception;

public class XpathException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public XpathException(String message)
    {
        super(message);
    }

    public XpathException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
