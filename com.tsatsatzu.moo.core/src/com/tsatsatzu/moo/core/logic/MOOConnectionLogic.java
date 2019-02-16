package com.tsatsatzu.moo.core.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.data.MOOCommand;
import com.tsatsatzu.moo.core.data.MOOConnection;
import com.tsatsatzu.moo.core.data.MOOConnectionPoint;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

public class MOOConnectionLogic
{
    private static Map<Object,MOOConnectionPoint> mPoints = new HashMap<>();
    private static int mNextConnectionID = -100;
    private static List<MOOConnection> mConnections = new ArrayList<>();
    
    public static int listenTCPIP(MOOObjRef handler, int port) throws MOOException
    {
        for (MOOConnectionPoint p : mPoints.values())
            if (p.getHandler().equals(handler))
                throw new MOOException("Already listing for this handler");
        final MOOConnectionPoint conn = new MOOConnectionPoint();
        conn.setType(MOOConnectionPoint.TCPIP);
        conn.setCanon(port);
        conn.setHandler(handler);
        ServerSocket server;
        try
        {
            server = new ServerSocket(port);
        }
        catch (IOException e)
        {
            throw new MOOException("Error listening on port "+port, e);
        }
        Thread t = new Thread("Server Listener for "+port) { public void run() { doListen(conn); } };
        conn.setServer(server);
        conn.setService(t);
        mPoints.put(conn.getCanon(), conn);
        t.start();
        return conn.getCanon();
    }

    public static void terminateTCPIP(int value) throws MOOException
    {
        MOOConnectionPoint conn = mPoints.get(value);
        if (conn == null)
            return;
        try
        {
            conn.getServer().close();
        }
        catch (IOException e)
        {
            throw new MOOException(e);
        }     
    }
    
    private static void doListen(MOOConnectionPoint point)
    {
        MOOOpsLogic.log("Starting to listen on "+point.getCanon());
        for (;;)
        {
            try
            {
                Socket sock = point.getServer().accept();
                final MOOConnection conn = new MOOConnection();
                conn.setType(MOOConnection.TCPIP);
                conn.setConnection(sock);
                conn.setPlayer(new MOOObjRef(mNextConnectionID--));
                conn.setPoint(point);
                Thread t = new Thread("Connection listener for "+point.getCanon()+" #"+conn.getPlayer().getValue())
                        {public void run() { doConnection(conn); } };
                conn.setService(t);
                mConnections.add(conn);
                t.start();
            }
            catch (IOException e)
            {
                if (!(e instanceof SocketException))
                    MOOOpsLogic.log(e);
                break;
            }
        }
        MOOOpsLogic.log("Done listening to "+point.getCanon()+".");
        try
        {
            point.getServer().close();
        }
        catch (IOException e)
        {
            if (!(e instanceof SocketException))
                MOOOpsLogic.log(e);
        }
        mPoints.remove(point.getCanon());
    }
    
    private static void doConnection(MOOConnection conn)
    {
        MOOOpsLogic.log("New connection received.");
        BufferedReader rdr;
        try
        {
            rdr = new BufferedReader(new InputStreamReader(conn.getConnection().getInputStream()));
            BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(conn.getConnection().getOutputStream()));
            conn.setWriter(wtr);
            doLoginCommand(conn, "");
        }
        catch (Exception e)
        {
            MOOOpsLogic.log("Error on connection", e);
            return;
        }
        String outOfBandPrefix = System.getProperty("moo.out-of-band.prefix", "#$#");
        for (;;)
        {
            try
            {
                String inbuf = rdr.readLine();
                if (inbuf == null)
                {
                    MOOOpsLogic.log("End of input detected");
                    break;
                }
                MOOOpsLogic.log("Received '"+inbuf+"'");
                if (conn.isProgramMode())
                    doProgram(conn, inbuf);
                else if (inbuf.startsWith(outOfBandPrefix))
                    doOutOfBandCommand(conn, inbuf);
                else if (!conn.isLoggedIn())
                    doLoginCommand(conn, inbuf);
                else
                    doCommand(conn, inbuf);
            }
            catch (Exception e)
            {
                MOOOpsLogic.log("Error reading from connection", e);
                MOOObject h = MOODbLogic.get(conn.getPoint().getHandler());
                try
                {
                    MOOScriptLogic.executeScriptMaybe(conn.getPlayer(), h, "user_client_disconnected", conn.getPlayer()); 
                }
                catch (MOOException e2)
                {                    
                }
                break;
            }
        }
        MOOOpsLogic.log("Closing connection.");
        try
        {
            conn.getConnection().close();
        }
        catch (IOException e)
        {
        }
        mConnections.remove(conn);
    }

    private static void doOutOfBandCommand(MOOConnection conn, String inbuf)
            throws MOOException, IOException
    {
        MOOObject h = MOODbLogic.get(conn.getPoint().getHandler());
        StringTokenizer st = new StringTokenizer(inbuf, " \t");
        Object[] args = new Object[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            args[i] = st.nextToken();
        MOOScriptLogic.executeScriptMaybe(conn.getPlayer(), h, "do_out_of_band_command", args);
    }

    private static void doLoginCommand(MOOConnection conn, String inbuf)
            throws MOOException, IOException
    {
        MOOObject h = MOODbLogic.get(conn.getPoint().getHandler());
        StringTokenizer st = new StringTokenizer(inbuf, " \t");
        Object[] args = new Object[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            args[i] = st.nextToken();
        int maxBefore = MOOObjectAPI.max_object().getValue().intValue();
        MOOValue val = MOOScriptLogic.executeScript(conn.getPlayer(), h, "do_login_command", args);
        if (val instanceof MOOObjRef)
        {
            MOOObject player = MOODbLogic.get((MOOObjRef)val);
            if (player.isPlayer())
            {
                conn.setPlayer(new MOOObjRef(player.getOID()));
                if (player.getOID() > maxBefore)
                    MOOScriptLogic.executeScriptMaybe(conn.getPlayer(), h, "user_created", player); 
                else
                {
                    String cmd = "user_connected";
                    for (MOOConnection c : mConnections)
                        if ((c != conn) && c.getPlayer().equals(player))
                        {
                            c.getConnection().close();
                            cmd = "user_reconnected";
                            break;
                        }
                    MOOScriptLogic.executeScriptMaybe(conn.getPlayer(), h, cmd, player); 
                }
            }
        }
    }

    private static void doCommand(MOOConnection conn, String inbuf)
            throws MOOException
    {
        MOOObject h = MOODbLogic.get(conn.getPoint().getHandler());
        MOOCommand cmd = MOOParseLogic.parse(h, conn.getPlayer(), inbuf);
        switch (cmd.getType())
        {
            case MOOCommand.PROGRAM:
                conn.setProgramMode(true);
                conn.getProgram().setLength(0);
                break;
            case MOOCommand.PREFIX:
                if (cmd.getArgs().size() == 1)
                    conn.setPrefix("");
                else
                    conn.setPrefix(cmd.getArgs().get(1));
                break;
            case MOOCommand.SUFFIX:
                if (cmd.getArgs().size() == 1)
                    conn.setSuffix("");
                else
                    conn.setSuffix(cmd.getArgs().get(1));
                break;
            case MOOCommand.FLUSH:
                conn.flush();
                break;
            case MOOCommand.NORMAL:
                conn.print(conn.getPrefix());
                MOOScriptLogic.executeCommand(cmd);
                conn.print(conn.getSuffix());
                break;
            case MOOCommand.NONE:
                MOOProgrammerLogic.pushProgrammer(conn.getPlayer());
                try
                {
                    MOOObject location = MOODbLogic.get(MOODbLogic.get(conn.getPlayer()).getLocation());
                    if ((location != null) && (location.getVerb("huh") != null))
                        MOOScriptLogic.executeScript(conn.getPlayer(), location, "huh");
                    else
                        conn.println("I didn't understand that.");
                }
                finally
                {
                    MOOProgrammerLogic.popProgrammer();
                }
                break;
        }
    }

    private static void doProgram(MOOConnection conn, String inbuf)
            throws MOOException
    {
        if (!".".equals(inbuf.trim()))
        {
            conn.getProgram().append(inbuf+"\n");
            return;
        }
        String script = conn.getProgram().toString();
        conn.getProgram().setLength(0);
        conn.setProgramMode(false);
        try
        {
            MOOValue val = MOOScriptLogic.executeScript(conn.getPlayer(), script);
            if (val == null)
                conn.println("Return: <null>");
            else
                conn.println("Return: "+val.toString());
        }
        catch (MOOException e)
        {
            
        }
    }

    public static MOOConnection findConnection(MOOObjRef ref)
    {
        for (MOOConnection conn : mConnections)
            if (ref.equals(conn.getPlayer()))
                return conn;
        return null;
    }
}
