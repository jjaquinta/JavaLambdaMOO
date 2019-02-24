package com.tsatsatzu.moo.core.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.data.IConnectionHandler;
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
    private static List<IConnectionHandler> mHandlers = new ArrayList<>();
    
    public static void initialize()
    {
        for (Object k : System.getProperties().keySet())
        {
            String key = k.toString();
            if (key.startsWith("moo.connection."))
                try
                {
                    String hClassName = System.getProperty(key);
                    Class<?> hClass = Class.forName(hClassName);
                    IConnectionHandler h = (IConnectionHandler)hClass.newInstance();
                    mHandlers.add(h);
                }
                catch (Exception e)
                {
                    MOOOpsLogic.log("Failure to create connection point handler '"+key+"'", e);
                }
        }
        if (mHandlers.size() == 0)
            mHandlers.add(new TCPIPConnectionHandler());
    }
    
    public static void shutdown()
    {
        for (Iterator<MOOConnection> i = mConnections.iterator(); i.hasNext(); )
        {
            try
            {
                i.next().close();
            }
            catch (MOOException e)
            {
                MOOOpsLogic.log(e);
            }
            i.remove();
        }
        mPoints.clear();
        mHandlers.clear();
    }
    
    public static void addConnectionHandler(IConnectionHandler h)
    {
        mHandlers.add(h);
    }
    
    public static int listen(MOOObjRef handler, String spec, boolean print) throws MOOException
    {
        for (MOOConnectionPoint p : mPoints.values())
            if (p.getHandler().equals(handler))
                throw new MOOException("Already listing for this handler");
        IConnectionHandler connHandler = null;
        for (IConnectionHandler h : mHandlers)
            if (h.isHandlerFor(spec))
            {
                connHandler = h;
                break;
            }
        if (connHandler == null)
            throw new MOOException("No connection handler for '"+spec+"'");
        final MOOConnectionPoint conn = connHandler.setupPoint(spec);
        conn.setHandler(handler);
        conn.setPrintMessages(print);
        mPoints.put(conn.getCanon(), conn);
        conn.open();
        Thread t = new Thread("Server Listener for "+conn.getCanon()) { public void run() { doListen(conn); } };
        t.start();
        conn.setService(t);
        return conn.getCanon();
    }

    public static void terminate(int value) throws MOOException
    {
        MOOConnectionPoint conn = mPoints.get(value);
        if (conn == null)
            return;
        conn.close();
    }
    
    public static Collection<MOOConnectionPoint> getConnectionPoints()
    {
        return mPoints.values();
    }
    
    private static void doListen(MOOConnectionPoint point)
    {
        MOOOpsLogic.log("Starting to listen on "+point.getCanon());
        for (;;)
        {
            try
            {
                final MOOConnection conn = point.waitForConnection();
                if (conn == null)
                    break;
                conn.setPlayer(new MOOObjRef(mNextConnectionID--));
                conn.setPoint(point);
                conn.setConnectedAt(System.currentTimeMillis());
                Thread t = new Thread("Connection listener for "+point.getCanon()+" #"+conn.getPlayer().getValue())
                        {public void run() { doConnection(conn); } };
                conn.setService(t);
                mConnections.add(conn);
                t.start();
            }
            catch (MOOException e)
            {
                MOOOpsLogic.log(e);
                break;
            }
        }
        MOOOpsLogic.log("Done listening to "+point.getCanon()+".");
        try
        {
            point.close();
        }
        catch (MOOException e)
        {
            MOOOpsLogic.log(e);
        }
        mPoints.remove(point.getCanon());
    }
    
    private static void doConnection(MOOConnection conn)
    {
        MOOOpsLogic.log("New connection received.");
        try
        {
            doLoginCommand(conn, "");
        }
        catch (MOOException | IOException e1)
        {
            MOOOpsLogic.log("Error initializing connection", e1);
            return;
        }
        String outOfBandPrefix = System.getProperty("moo.out-of-band.prefix", "#$#");
        for (;;)
        {
            try
            {
                String inbuf = conn.readLine();
                if (inbuf == null)
                {
                    MOOOpsLogic.log("End of input detected");
                    break;
                }
                conn.setActiveAt(System.currentTimeMillis());
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
            conn.close();
        }
        catch (MOOException e)
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
        if ((val instanceof MOOObjRef) && !((MOOObjRef)val).isNone())
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
                            c.close();
                            cmd = "user_reconnected";
                            break;
                        }
                    MOOScriptLogic.executeScriptMaybe(conn.getPlayer(), h, cmd, player.toRef()); 
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
            MOOProgrammerLogic.pushProgrammer(conn.getPlayer());
            MOOValue val = MOOScriptLogic.executeScript(script);
            if (val == null)
                conn.println("Return: <null>");
            else
                conn.println("Return: "+val.toString());
        }
        catch (MOOException e)
        {
            
        }
        finally 
        {
            MOOProgrammerLogic.popProgrammer();
        }
    }

    public static MOOConnection findConnection(MOOObjRef ref)
    {
        for (MOOConnection conn : mConnections)
            if (ref.equals(conn.getPlayer()))
                return conn;
        return null;
    }

    public static List<Integer> countPlayers(boolean all)
    {
        List<Integer> ret = new ArrayList<>();
        for (MOOConnection conn : mConnections)
        {
            MOOObjRef player = conn.getPlayer();
            if ((player.getValue() > 0) || all)
                ret.add(player.getValue());
        }
        return ret;
    }
}
