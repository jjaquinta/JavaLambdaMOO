package com.tsatsatzu.moo.core.logic.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.logic.IMOOStore;

public class MemStore implements IMOOStore
{
    private int mMaxObject;
    private Map<Integer, MOOObject> mCache = new HashMap<>();
    private List<Integer> mPlayers = new ArrayList<>();

    @Override
    public void initialize() throws MOOException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void shutdown() throws MOOException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public MOOObject getInstance(int oid)
    {
        return mCache.get(oid);
    }

    @Override
    public MOOObject newInstance(int parentOid)
    {
        MOOObject obj = new MOOObject();
        obj.setOID(++mMaxObject);
        obj.setParent(parentOid);
        mCache.put(obj.getOID(), obj);
        return obj;
    }

    @Override
    public void delInstance(int oid)
    {
        mCache.remove(oid);
    }

    @Override
    public void save(int oid)
    {
        MOOObject inst = mCache.get(oid);
        if (inst.isPlayer())
            mPlayers.add(inst.getOID());
        else if (mPlayers.contains(inst.getOID()))
            mPlayers.remove((Integer)inst.getOID());
    }

    @Override
    public boolean isValid(int oid)
    {
        return mCache.containsKey(oid);
    }
    
    @Override
    public int[] getPlayers()
    {
        Integer[] players = mPlayers.toArray(new Integer[0]);
        int[] ret = new int[players.length];
        for (int i = 0; i < players.length; i++)
            ret[i] = players[i];
        return ret;
    }

    public int getMaxObject()
    {
        return mMaxObject;
    }

    void setMaxObject(int maxObject)
    {
        mMaxObject = maxObject;
    }

    List<Integer> getPlayerList()
    {
        return mPlayers;
    }

    void setPlayerList(List<Integer> players)
    {
        mPlayers = players;
    }

    Map<Integer, MOOObject> getCache()
    {
        return mCache;
    }

    void setCache(Map<Integer, MOOObject> cache)
    {
        mCache = cache;
    }

}
