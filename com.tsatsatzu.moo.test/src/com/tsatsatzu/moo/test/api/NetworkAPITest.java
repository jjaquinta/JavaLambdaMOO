package com.tsatsatzu.moo.test.api;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.api.MOONetworkAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOOConnectionLogic;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;
import com.tsatsatzu.moo.test.MinimalDBBase;
import com.tsatsatzu.moo.test.util.TestConnectionHandler;

public class NetworkAPITest extends MinimalDBBase
{
    @Test
    public void testConnectedPlayers() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        MOOValue connected1 = MOOScriptLogic.executeScript(mProgrammer, "connected_players(true);");
        Assert.assertTrue("Should return List", connected1 instanceof MOOList);
        Assert.assertEquals("Should be no one connected", 0, ((MOOList)connected1).getValue().size());
        conn.startConnection();
        conn.write("xyzzy");
        conn.read();
        MOOValue connected2 = MOOScriptLogic.executeScript(mProgrammer, "connected_players(true);");
        Assert.assertTrue("Should return List", connected2 instanceof MOOList);
        Assert.assertEquals("Should be one connected", 1, ((MOOList)connected2).getValue().size());
    }
    @Test
    public void testListen() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOOScriptLogic.executeScript(mProgrammer, "listen('#0', 'test://1', true);");
        conn.startConnection();
        conn.write("xyzzy");
        String huh = conn.read();
        Assert.assertEquals("Should be no such command text", "I didn't understand that.", huh);
    }
}
