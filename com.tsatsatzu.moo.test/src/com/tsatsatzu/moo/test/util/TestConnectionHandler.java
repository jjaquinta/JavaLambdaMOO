package com.tsatsatzu.moo.test.util;

import java.util.ArrayList;
import java.util.List;

import com.tsatsatzu.moo.core.data.IConnectionHandler;
import com.tsatsatzu.moo.core.data.MOOConnection;
import com.tsatsatzu.moo.core.data.MOOConnectionPoint;
import com.tsatsatzu.moo.core.data.MOOException;

import jo.util.utils.obj.IntegerUtils;

public class TestConnectionHandler implements IConnectionHandler
{
    private MOOTestConnection   mConnection = null;
    private List<String>        mToServer = new ArrayList<>();
    private List<String>        mToClient = new ArrayList<>();
    private boolean             mPointOpen;
    private boolean             mConnectionOpen;

    @Override
    public boolean isHandlerFor(String spec)
    {
        return spec.startsWith("test://");
    }

    @Override
    public MOOConnectionPoint setupPoint(String spec)
    {
        if (spec.startsWith("test://"))
            spec = spec.substring(7);
        int port = IntegerUtils.parseInt(spec);
        MOOConnectionPoint conn = new MOOTestConnnectionPoint();
        conn.setCanon(port);
        return conn;
    }

    class MOOTestConnnectionPoint extends MOOConnectionPoint
    {
        @Override
        public void open() throws MOOException
        {
            mConnection = null;
            mPointOpen = true;;
        }

        @Override
        public void close() throws MOOException
        {
            mConnection = null;
            mPointOpen = false;
        }

        @Override
        public MOOConnection waitForConnection() throws MOOException
        {
            while (mConnection == null)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                }
                if (mPointOpen == false)
                    return null;
            }
            MOOConnection conn = mConnection;
            mConnection = null;
            mConnectionOpen = true;
            return conn;
        }        
    }
    
    class MOOTestConnection extends MOOConnection
    {
        @Override
        public void print(String msg) throws MOOException
        {
            mToClient.add(msg);
        }

        @Override
        public void println(String msg) throws MOOException
        {
            mToClient.add(msg);
        }

        @Override
        public void flush() throws MOOException
        {
        }

        @Override
        public String readLine() throws MOOException
        {
            while (mToServer.size() == 0)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                }
                if (mPointOpen == false)
                    return null;
                if (mConnectionOpen == false)
                    return null;
            }
            String line = mToServer.get(0);
            mToServer.remove(0);
            return line;
        }

        @Override
        public void close() throws MOOException
        {
            mConnectionOpen = false;
        }
        
    }
    
    public void startConnection()
    {
        mConnection = new MOOTestConnection();
    }
    
    public void write(String line)
    {
        mToServer.add(line);
    }
    
    public String read()
    {
        while (mToClient.size() == 0)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }
        String line = mToClient.get(0);
        mToClient.remove(0);
        return line;
    }
}
