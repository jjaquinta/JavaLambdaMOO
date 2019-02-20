package com.tsatsatzu.moo.core.logic.mem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.logic.IMOOStore;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;

import jo.audio.util.IJSONAble;
import jo.audio.util.JSONUtils;
import jo.util.utils.obj.IntegerUtils;

public class MemStore implements IMOOStore, IJSONAble
{
    private File    mDiskImage;
    private int mMaxObject;
    private Map<Integer, MOOObject> mCache = new HashMap<>();
    private List<Integer> mPlayers = new ArrayList<>();

    @Override
    public void initialize() throws MOOException
    {
        String diskImageName = System.getProperty("moo.store.mem.disk");
        if (diskImageName != null)
        {
            mDiskImage = new File(diskImageName);
            if (mDiskImage != null)
                loadFromDisk();
        }
    }
    
    private void loadFromDisk()
    {
        if (mDiskImage == null)
            return;
        try
        {
            JSONObject json = JSONUtils.readJSON(mDiskImage);
            fromJSON(json);
        }
        catch (IOException e)
        {
            MOOOpsLogic.log("Error reading from "+mDiskImage, e);
        }
    }
    
    private int saveToDisk()
    {
        if (mDiskImage == null)
            return 1;
        JSONObject json = toJSON();
        try
        {
            JSONUtils.writeJSON(mDiskImage, json);
            return 1;
        }
        catch (IOException e)
        {
            MOOOpsLogic.log("Error saving to "+mDiskImage, e);
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("maxObject", mMaxObject);
        JSONObject cache = new JSONObject();
        json.put("cache", cache);
        for (Integer oid : mCache.keySet())
        {
            MOOObject obj = mCache.get(oid);
            json.put(oid.toString(), obj.toJSON());
        }
        JSONArray players = new JSONArray();
        json.put("players", players);
        for (Integer player : mPlayers)
            players.add(player);
        return json;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        JSONObject cache = JSONUtils.getObject(json, "cache");
        mMaxObject = 0;
        mPlayers.clear();
        for (String key : cache.keySet())
        {
            JSONObject o = (JSONObject)cache.get(key);
            int oid = IntegerUtils.parseInt(key);
            MOOObject obj = new MOOObject();
            obj.fromJSON(o);
            mCache.put(oid, obj);
            mMaxObject = Math.max(mMaxObject, oid);
            if (obj.isPlayer())
                mPlayers.add(oid);
        }
    }

    @Override
    public void shutdown() throws MOOException
    {
    }
    
    @Override
    public int checkpoint()
    {        
        return saveToDisk();
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
    public void markDirty(int oid)
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

    @Override
    public String getServerVersion()
    {
        return "MemStore 1.0";
    }

}
