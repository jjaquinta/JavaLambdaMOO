package com.tsatsatzu.moo.core.logic;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOFatalException;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

public class MOOOpsLogic
{
    private static Thread mInitThread = null;
    
    // kick off initialization
    public static void startInitialize()
    {
        if (mInitThread == null)
        {
            mInitThread = new Thread("initialize") { public void run() { doInitialize(); } };
            mInitThread.start();
        }
    }
    
    // block until initialization complete
    public static void initailize() throws InterruptedException
    {
        if (mInitThread == null)
            startInitialize();
        mInitThread.join();
    }
    
    private static void doInitialize()
    {
        try
        {
            try
            {
                MOODbLogic.initailize();
                MOOConnectionLogic.initialize();
            }
            catch (MOOFatalException e)
            {
                log("Fatal error during initialization", e);
                System.exit(0);
            }
            MOOScriptLogic.executeScriptMaybe(MOOObjRef.NONE, MOODbLogic.get(0), "server_started");
        }
        catch (MOOException e)
        {
            log("Error during initialization", e);
            e.printStackTrace();
        }
        mInitThread = null;
    }

    private static Thread mTermThread = null;
    
    // kick off shutdown
    public static void startShutdown()
    {
        if (mTermThread == null)
        {
            mTermThread = new Thread("terminate") { public void run() { doShutdown(); } };
            mTermThread.start();
        }
    }
    
    // block until shutdown complete
    public static void shutdown() throws InterruptedException
    {
        if (mTermThread == null)
            startShutdown();
        mTermThread.join();
    }
    
    private static void doShutdown()
    {
        try
        {
            try
            {
                MOOConnectionLogic.shutdown();
                MOODbLogic.shutdown();
            }
            catch (MOOFatalException e)
            {
                log("Fatal error during initialization", e);
                System.exit(0);
            }
        }
        catch (MOOException e)
        {
            log("Error during initialization", e);
            e.printStackTrace();
        }
    }
    
    public static void log(String txt)
    {
        log(txt, null);
    }
    
    public static void log(Throwable t)
    {
        log(null, t);
    }
    
    public static void log(String txt, Throwable t)
    {
        if (txt != null)
            System.out.println(txt);
        if (t != null)
            t.printStackTrace();
    }
}
