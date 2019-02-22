package com.tsatsatzu.moo.test;

import org.junit.After;
import org.junit.Before;

import com.tsatsatzu.moo.core.api.MOONetworkAPI;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOOConnectionLogic;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;
import com.tsatsatzu.moo.test.util.TestConnectionHandler;

public class VoiceDBBase
{
    protected MOOObjRef mProgrammer;
    protected TestConnectionHandler mConnection;

    @Before
    public void setUp() throws Exception
    {
        System.setProperty("moo.store.class", "com.tsatsatzu.moo.test.util.VoiceJDBStore");
        mConnection = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(mConnection);
        MOOOpsLogic.initailize();
        int oid = MOODbLogic.getPlayers()[0];
        mProgrammer = new MOOObjRef(oid);
        MOOProgrammerLogic.pushProgrammer(mProgrammer);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
    }
    
    @After
    public void setDown() throws Exception
    {
        MOOProgrammerLogic.popProgrammer();
        MOOOpsLogic.shutdown();
    }
}
