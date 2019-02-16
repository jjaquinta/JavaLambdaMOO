package com.tsatsatzu.moo.core.logic;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;

public interface IMOOStore
{
    public void      initialize() throws MOOException;
    public void      shutdown() throws MOOException;
    
    public MOOObject getInstance(int oid);
    public MOOObject newInstance(int parentOid);
    public void      delInstance(int oid);
    
    public void      save(int oid);
    
    public boolean   isValid(int oid);
    
    public int[]     getPlayers();
    public int       getMaxObject();
}
