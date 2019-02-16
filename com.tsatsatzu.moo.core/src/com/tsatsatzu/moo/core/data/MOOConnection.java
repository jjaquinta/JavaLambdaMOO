package com.tsatsatzu.moo.core.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public class MOOConnection
{
    public static final int    TCPIP = 0;

    private int                mType;
    private MOOObjRef          mPlayer;
    private Socket             mConnection;
    private Thread             mService;
    private MOOConnectionPoint mPoint;
    private BufferedWriter     mWriter;
    private boolean            mProgramMode = false;
    private StringBuffer       mProgram = new StringBuffer();
    private String             mPrefix;
    private String             mSuffix;

    // utilities
    public boolean isLoggedIn()
    {
        return mPlayer.getValue() >= 0;
    }
    
    public void print(String msg) throws MOOException
    {
        try
        {
            if (msg != null)
                mWriter.write(msg);
            mWriter.flush();
        }
        catch (IOException e)
        {
            throw new MOOException("Error printing '"+msg+"' to connection", e);
        }
    }
    
    public void println(String msg) throws MOOException
    {
        try
        {
            if (msg != null)
                mWriter.write(msg);
            mWriter.newLine();
            mWriter.flush();
        }
        catch (IOException e)
        {
            throw new MOOException("Error printing '"+msg+"' to connection", e);
        }
    }
    
    public void flush() throws MOOException
    {
        try
        {
            mWriter.flush();
        }
        catch (IOException e)
        {
            throw new MOOException("Error flushing connection", e);
        }
    }

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

    public Socket getConnection()
    {
        return mConnection;
    }

    public void setConnection(Socket connection)
    {
        mConnection = connection;
    }

    public MOOConnectionPoint getPoint()
    {
        return mPoint;
    }

    public void setPoint(MOOConnectionPoint point)
    {
        mPoint = point;
    }

    public BufferedWriter getWriter()
    {
        return mWriter;
    }

    public void setWriter(BufferedWriter writer)
    {
        mWriter = writer;
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
}
