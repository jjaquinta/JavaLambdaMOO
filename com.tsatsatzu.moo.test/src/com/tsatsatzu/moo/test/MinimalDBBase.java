package com.tsatsatzu.moo.test;

import org.junit.After;
import org.junit.Before;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MinimalDBBase
{
    protected MOOObjRef mProgrammer;

    @Before
    public void setUp() throws Exception
    {
        System.setProperty("moo.store.class", "com.tsatsatzu.moo.test.util.MinimalJDBStore");
        MOOOpsLogic.initailize();
        int oid = MOODbLogic.getPlayers()[0];
        mProgrammer = new MOOObjRef(oid);
        MOOProgrammerLogic.pushProgrammer(mProgrammer);
    }
    
    @After
    public void setDown() throws Exception
    {
        MOOProgrammerLogic.popProgrammer();
        MOOOpsLogic.shutdown();
    }
}
