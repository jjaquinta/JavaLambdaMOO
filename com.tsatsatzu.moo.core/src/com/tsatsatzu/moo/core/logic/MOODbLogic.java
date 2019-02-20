package com.tsatsatzu.moo.core.logic;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOFatalException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOProperty;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

public class MOODbLogic
{
    private static IMOOStore   mStore;
    private static boolean     mShutdown;
    private static boolean     mIsSaving;
    private static Thread      mSave;
    
    // block until initialization complete
    public static void initailize() throws MOOException
    {
        String storeClassName = System.getProperty("moo.store.class", "com.tsatsatzu.moo.core.logic.mem.MemStore");
        Class<?> storeClass;
        try
        {
            storeClass = Class.forName(storeClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new MOOFatalException("Cannot load store class '"+storeClassName+"'", e);
        }
        try
        {
            mStore = (IMOOStore)storeClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new MOOFatalException("Cannot instantiate store class '"+storeClassName+"'", e);
        }
        mStore.initialize();
        mSave = new Thread("databaseSave") { public void run() { doSaveProcess(); } };
        mSave.start();
    }

    // block until shutdown complete
    public static void shutdown() throws MOOException
    {
        mShutdown = true;
        mSave.interrupt();
        mStore.checkpoint();
        mStore.shutdown();
        mStore = null;
    }

    private static void doSaveProcess()
    {
        while (!mShutdown)
        {
            long sleep = 60*60*1000L;
            MOOObject root = mStore.getInstance(0);
            MOOProperty di = root.getProperties().get("dump_interval");
            if (di != null)
            {
                MOOValue sl = di.getValue();
                if (sl instanceof MOONumber)
                {
                    int seconds = ((MOONumber)sl).getValue().intValue();
                    if (seconds > 0)
                        sleep = seconds*1000L;
                }
            }
            try
            {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e)
            {
                if (mShutdown)
                    break;
            }
            try
            {
                MOOScriptLogic.executeScriptMaybe(MOOObjRef.NONE, get(0), "checkpoint_started");
            }
            catch (MOOException e)
            {
                MOOOpsLogic.log("Error invoking checkpoint_started before save", e);
            }
            mIsSaving = true;
            int passed = mStore.checkpoint();
            mIsSaving = false;
            try
            {
                MOOScriptLogic.executeScriptMaybe(MOOObjRef.NONE, get(0), "checkpoint_finished", new MOONumber(passed));
            }
            catch (MOOException e)
            {
                MOOOpsLogic.log("Error invoking checkpoint_finished after save", e);
            }
        }
    }
    
    public static boolean isValid(MOOObject parent)
    {
        return mStore.isValid(parent.getOID());
    }

    public static MOOObject get(MOOObjRef ref)
    {
        if (ref == null)
            return null;
        return get(ref.getValue());
    }

    public static MOOObject get(int oid)
    {
        if (oid == -1)
            return null;
        return mStore.getInstance(oid);
    }

    public static MOOObject newInstance(int parentOid)
    {
        return mStore.newInstance(parentOid);
    }

    public static void markDirty(int oid)
    {
        mStore.markDirty(oid);
    }

    public static void markDirty(MOOObjRef ref)
    {
        markDirty(ref.getValue());
    }

    public static void recycle(int oid)
    {
        mStore.delInstance(oid);
    }

    public static int getMaxObject()
    {
        return mStore.getMaxObject();
    }
    
    public static int[] getPlayers()
    {
        return mStore.getPlayers();
    }

    public static String serverVersion()
    {
        return mStore.getServerVersion();
    }

    public static void checkpoint()
    {
        if (!mIsSaving)
            mSave.interrupt();
    }
}
