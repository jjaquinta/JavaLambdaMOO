package com.tsatsatzu.moo.core.logic;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOFatalException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public class MOODbLogic
{
    private static IMOOStore   mStore;
    
    // block until initialization complete
    public static void initailize() throws MOOException
    {
        String storeClassName = System.getProperty("moo.store.class", "com.tsatsatzu.moo.core.logic.disk.DISKStore");
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
    }

    // block until shutdown complete
    public static void shutdown() throws MOOException
    {
        mStore.shutdown();
        mStore = null;
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
        mStore.save(oid);
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
}
