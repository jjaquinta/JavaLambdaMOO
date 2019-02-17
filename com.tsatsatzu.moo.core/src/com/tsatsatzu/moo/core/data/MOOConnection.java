package com.tsatsatzu.moo.core.data;

import java.util.HashMap;
import java.util.Map;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public abstract class MOOConnection
{
    public static final int       TCPIP        = 0;

    private int                   mType;
    private MOOObjRef             mPlayer;
    private Thread                mService;
    private MOOConnectionPoint    mPoint;
    private boolean               mProgramMode = false;
    private StringBuffer          mProgram     = new StringBuffer();
    private String                mPrefix;
    private String                mSuffix;
    private long                  mConnectedAt;
    private long                  mActiveAt;
    private Map<String, MOOValue> mOptions     = new HashMap<>();

    // utilities
    public boolean isLoggedIn()
    {
        return mPlayer.getValue() >= 0;
    }

    public abstract void print(String msg) throws MOOException;

    public abstract void println(String msg) throws MOOException;

    public abstract void flush() throws MOOException;

    public abstract String readLine() throws MOOException;

    public abstract void close() throws MOOException;

    // getters and setters
    public int getType()
    {
        return mType;
    }

    public void setType(int type)
    {
        mType = type;
    }

    public Thread getService()
    {
        return mService;
    }

    public void setService(Thread service)
    {
        mService = service;
    }

    public MOOObjRef getPlayer()
    {
        return mPlayer;
    }

    public void setPlayer(MOOObjRef player)
    {
        mPlayer = player;
    }

    public MOOConnectionPoint getPoint()
    {
        return mPoint;
    }

    public void setPoint(MOOConnectionPoint point)
    {
        mPoint = point;
    }

    public boolean isProgramMode()
    {
        return mProgramMode;
    }

    public void setProgramMode(boolean programMode)
    {
        mProgramMode = programMode;
    }

    public StringBuffer getProgram()
    {
        return mProgram;
    }

    public void setProgram(StringBuffer program)
    {
        mProgram = program;
    }

    public String getPrefix()
    {
        return mPrefix;
    }

    public void setPrefix(String prefix)
    {
        mPrefix = prefix;
    }

    public String getSuffix()
    {
        return mSuffix;
    }

    public void setSuffix(String suffix)
    {
        mSuffix = suffix;
    }

    public long getConnectedAt()
    {
        return mConnectedAt;
    }

    public void setConnectedAt(long connectedAt)
    {
        mConnectedAt = connectedAt;
    }

    public long getActiveAt()
    {
        return mActiveAt;
    }

    public void setActiveAt(long activeAt)
    {
        mActiveAt = activeAt;
    }

    public Map<String, MOOValue> getOptions()
    {
        return mOptions;
    }

    public void setOptions(Map<String, MOOValue> options)
    {
        mOptions = options;
    }
}
