package com.tsatsatzu.moo.core.data;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public abstract class MOOConnectionPoint
{
    public static final int    TCPIP = 0;

    private int                mType;
    private int                mCanon;
    private MOOObjRef          mHandler;
    private Thread             mService;

    // utilities
    
    public abstract void open() throws MOOException;
    public abstract void close() throws MOOException;
    public abstract MOOConnection waitForConnection() throws MOOException;

    // getters and setters
    public int getType()
    {
        return mType;
    }

    public void setType(int type)
    {
        mType = type;
    }

    public int getCanon()
    {
        return mCanon;
    }

    public void setCanon(int canon)
    {
        mCanon = canon;
    }

    public MOOObjRef getHandler()
    {
        return mHandler;
    }

    public void setHandler(MOOObjRef handler)
    {
        mHandler = handler;
    }

    public Thread getService()
    {
        return mService;
    }

    public void setService(Thread service)
    {
        mService = service;
    }
}
