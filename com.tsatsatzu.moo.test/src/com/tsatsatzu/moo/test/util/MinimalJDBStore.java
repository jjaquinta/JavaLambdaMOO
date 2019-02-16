package com.tsatsatzu.moo.test.util;

import java.io.File;
import java.io.IOException;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.logic.mem.MemImport;
import com.tsatsatzu.moo.core.logic.mem.MemStore;

public class MinimalJDBStore extends MemStore
{
    private static final String DB = "C:\\Users\\JoGrant\\git\\JavaLambdaMOO\\com.tsatsatzu.moo.core\\dbs\\minimal.jdb";
    
    @Override
    public void initialize() throws MOOException
    {
        super.initialize();
        try
        {
            MemImport.importFromDisk(this, new File(DB));
        }
        catch (IOException e)
        {
            throw new MOOException("Could not find: "+DB);
        }
    }
}
