package com.tsatsatzu.moo.test.util;

import java.io.File;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.logic.mem.MemStore;

public class VoiceJDBStore extends MemStore
{
    private static final String DB = "C:\\Users\\JoGrant\\git\\JavaLambdaMOO\\com.tsatsatzu.moo.core\\dbs\\VoiceCore.jdb";
    
    @Override
    public void initialize() throws MOOException
    {
        super.initialize();
        loadFromDisk(new File(DB));
    }
}
