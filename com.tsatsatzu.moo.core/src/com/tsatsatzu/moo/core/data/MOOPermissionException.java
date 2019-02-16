package com.tsatsatzu.moo.core.data;

@SuppressWarnings("serial")
public class MOOPermissionException extends MOOException
{
    public MOOPermissionException()
    {
        super();
    }

    public MOOPermissionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MOOPermissionException(String message)
    {
        super(message);
    }

    public MOOPermissionException(Throwable cause)
    {
        super(cause);
    }

}
