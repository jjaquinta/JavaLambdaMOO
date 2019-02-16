package com.tsatsatzu.moo.core.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MOOTCPIPConnection extends MOOConnection
{
    private Socket             mConnection;
    private BufferedReader     mReader;
    private BufferedWriter     mWriter;
    
    public MOOTCPIPConnection(Socket socket) throws MOOException
    {
        setType(MOOConnection.TCPIP);
        mConnection = socket;
        try
        {
            mReader = new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
            mWriter = new BufferedWriter(new OutputStreamWriter(mConnection.getOutputStream()));
        }
        catch (Exception e)
        {
            throw new MOOException(e);
        }
    }

    // utilities
    @Override
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
    
    @Override
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
    
    @Override
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
    
    @Override
    public String readLine() throws MOOException
    {
        try
        {
            return mReader.readLine();
        }
        catch (IOException e)
        {
            throw new MOOException(e);
        }
    }
    
    @Override
    public void close() throws MOOException
    {
        try
        {
            mConnection.close();
        }
        catch (IOException e)
        {
            throw new MOOException(e);
        }
    }

    // getters and setters
    public Socket getConnection()
    {
        return mConnection;
    }

    public void setConnection(Socket connection)
    {
        mConnection = connection;
    }

    public BufferedWriter getWriter()
    {
        return mWriter;
    }

    public void setWriter(BufferedWriter writer)
    {
        mWriter = writer;
    }
}
