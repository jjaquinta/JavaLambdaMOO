package com.tsatsatzu.moo.core.data;

@SuppressWarnings("serial")
public class MOOException extends Exception
{
    public MOOException()
    {
        super();
    }

    public MOOException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MOOException(String message)
    {
        super(message);
    }

    public MOOException(Throwable cause)
    {
        super(cause);
    }

}
