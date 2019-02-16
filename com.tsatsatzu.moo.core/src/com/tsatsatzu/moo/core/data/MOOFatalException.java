package com.tsatsatzu.moo.core.data;

@SuppressWarnings("serial")
public class MOOFatalException extends MOOException
{
    public MOOFatalException()
    {
        super();
    }

    public MOOFatalException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MOOFatalException(String message)
    {
        super(message);
    }

    public MOOFatalException(Throwable cause)
    {
        super(cause);
    }

}
