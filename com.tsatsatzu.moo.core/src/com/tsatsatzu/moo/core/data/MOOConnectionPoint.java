package com.tsatsatzu.moo.core.data;

import java.net.ServerSocket;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public class MOOConnectionPoint
{
    public static final int    TCPIP = 0;

    private int                mType;
    private int                mCanon;
    private MOOObjRef          mHandler;
    private ServerSocket       mServer;
    private Thread             mService;

    // utilities

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

    public ServerSocket getServer()
    {
        return mServer;
    }

    public void setServer(ServerSocket server)
    {
        mServer = server;
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
