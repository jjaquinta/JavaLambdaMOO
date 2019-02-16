package com.tsatsatzu.moo.core.data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MOOTCPIPConnectionPoint extends MOOConnectionPoint
{
    private ServerSocket       mServer;
    
    public MOOTCPIPConnectionPoint()
    {
        setType(MOOConnectionPoint.TCPIP);
    }

    // utilities
    @Override
    public void open() throws MOOException
    {
        ServerSocket server;
        try
        {
            server = new ServerSocket(getCanon());
        }
        catch (IOException e)
        {
            throw new MOOException("Error listening on port "+getCanon(), e);
        }
        setServer(server);
    }
    
    @Override
    public void close() throws MOOException
    {
        try
        {
            getServer().close();
        }
        catch (IOException e)
        {
            throw new MOOException(e);
        }     
    }
    
    @Override
    public MOOConnection waitForConnection() throws MOOException
    {
        try
        {
            Socket sock = getServer().accept();
            MOOTCPIPConnection conn = new MOOTCPIPConnection(sock);
            return conn;
        }
        catch (IOException e)
        {
            if (e.getMessage().equals("socket closed"))
                return null;
            throw new MOOException(e);
        }
    }

    // getters and setters

    public ServerSocket getServer()
    {
        return mServer;
    }

    public void setServer(ServerSocket server)
    {
        mServer = server;
    }

}
