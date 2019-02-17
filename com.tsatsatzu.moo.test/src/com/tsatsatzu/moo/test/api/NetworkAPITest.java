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
    public void testConnectedSeconds() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        try
        {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e)
        {
        }
        conn.write("xyzzy");
        conn.read();
        try
        {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e)
        {
        }
        MOOValue connected = MOOScriptLogic.executeScript(mProgrammer, "connected_seconds('#3');");
        Assert.assertTrue("Should return Number", connected instanceof MOONumber);
        Assert.assertEquals("Should be connected 4 seconds", 4, ((MOONumber)connected).getValue());
    }
    @Test
    public void testIdleSeconds() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        try
        {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e)
        {
        }
        conn.write("xyzzy");
        conn.read();
        try
        {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e)
        {
        }
        MOOValue connected = MOOScriptLogic.executeScript(mProgrammer, "idle_seconds('#3');");
        Assert.assertTrue("Should return Number", connected instanceof MOONumber);
        Assert.assertEquals("Should be idle 2 seconds", 2, ((MOONumber)connected).getValue());
    }
    @Test
    public void testNotify() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        conn.write("xyzzy");
        conn.read();
        MOOScriptLogic.executeScript(mProgrammer, "notify('#3', 'hello', true);");
        String reply = conn.read();
        Assert.assertEquals("Should be no such command text", "hello", reply);
    }
    @Test
    public void testOutputDelimiters() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        conn.write("PREFIX xyzzy");
        conn.write("SUFFIX yzzyx");
        conn.write("xyzzy");
        conn.read();
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "output_delimiters('#3');");
        Assert.assertTrue("Should return List", ret instanceof MOOList);
        Assert.assertEquals("Should be 2 items in list", 2, ((MOOList)ret).getValue().size());
        Assert.assertTrue("Item 1 should be string", ((MOOList)ret).getValue().get(0) instanceof MOOString);
        Assert.assertEquals("Item 1 should be prefix", "xyzzy", ((MOOString)((MOOList)ret).getValue().get(0)).getValue());
        Assert.assertTrue("Item 2 should be string", ((MOOList)ret).getValue().get(0) instanceof MOOString);
        Assert.assertEquals("Item 2 should be suffix", "xyzzy", ((MOOString)((MOOList)ret).getValue().get(0)).getValue());
    }
    @Test
    public void testBootPlayer() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        conn.write("xyzzy");
        conn.read();
        MOOList connected1 = MOONetworkAPI.connected_players(MOONumber.TRUE);
        Assert.assertEquals("Should be one connected", 1, connected1.getValue().size());
        MOOScriptLogic.executeScript(mProgrammer, "boot_player('#3');");
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
        }
        MOOList connected2 = MOONetworkAPI.connected_players(MOONumber.TRUE);
        Assert.assertEquals("Should be no one connected", 0, connected2.getValue().size());
    }
    @Test
    public void testSetConnectionOption() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        conn.write("xyzzy");
        conn.read();
        MOOScriptLogic.executeScript(mProgrammer, "set_connection_option('#3', 'wibble', 'wobble');");
        MOOValue ret = MOONetworkAPI.connection_option(new MOOObjRef(3), new MOOString("wibble"));
        Assert.assertTrue("Should return String", ret instanceof MOOString);
        Assert.assertEquals("Should be value we set", "wobble", ((MOOString)ret).getValue());
    }
    @Test
    public void testConnectionOptions() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        conn.write("xyzzy");
        conn.read();
        MOONetworkAPI.set_connection_option(new MOOObjRef(3), new MOOString("wibble"), new MOOString("wobble"));
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "connection_options('#3');");
        Assert.assertTrue("Should return List", ret instanceof MOOList);
        Assert.assertEquals("Should be single item in list", 1, ((MOOList)ret).getValue().size());
        MOOValue first = ((MOOList)ret).getValue().get(0);
        Assert.assertTrue("Should contain a list", first instanceof MOOList);
        Assert.assertEquals("Contained list should have two items", 2, ((MOOList)first).getValue().size());
        MOOValue firstKey = ((MOOList)first).getValue().get(0);
        Assert.assertTrue("Should contain a list", firstKey instanceof MOOString);
        Assert.assertEquals("Unexpected key", "wibble", ((MOOString)firstKey).getValue());
        MOOValue firstVal = ((MOOList)first).getValue().get(1);
        Assert.assertTrue("Should contain a list", firstVal instanceof MOOString);
        Assert.assertEquals("Unexpected key", "wobble", ((MOOString)firstVal).getValue());
    }
    @Test
    public void testConnectionOption() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        conn.startConnection();
        conn.write("xyzzy");
        conn.read();
        MOONetworkAPI.set_connection_option(new MOOObjRef(3), new MOOString("wibble"), new MOOString("wobble"));
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "connection_option('#3', 'wibble');");
        Assert.assertTrue("Should return String", ret instanceof MOOString);
        Assert.assertEquals("Unexpected option value", "wobble", ((MOOString)ret).getValue());
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
    @Test
    public void testunlisten() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        MOOScriptLogic.executeScript(mProgrammer, "unlisten(1);");
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
        }
        MOOList listeners = MOONetworkAPI.listeners();
        Assert.assertEquals("Unexpected listener", 0, listeners.getValue().size());
    }
    @Test
    public void testListeners() throws MOOException
    {
        TestConnectionHandler conn = new TestConnectionHandler();
        MOOConnectionLogic.addConnectionHandler(conn);
        MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("test://1"), MOONumber.TRUE);
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "listeners();");
        Assert.assertTrue("Should return List", ret instanceof MOOList);
        Assert.assertEquals("Should be single item in list", 1, ((MOOList)ret).getValue().size());
        MOOValue first = ((MOOList)ret).getValue().get(0);
        Assert.assertTrue("Should contain a list", first instanceof MOOList);
        Assert.assertEquals("Contained list should have three items", 3, ((MOOList)first).getValue().size());
        MOOValue firstHandler = ((MOOList)first).getValue().get(0);
        Assert.assertTrue("Should contain a list", firstHandler instanceof MOOObjRef);
        Assert.assertEquals("Unexpected handler", 0, ((MOOObjRef)firstHandler).getValue());
        MOOValue firstCanon = ((MOOList)first).getValue().get(1);
        Assert.assertTrue("Should contain a number", firstCanon instanceof MOONumber);
        Assert.assertEquals("Unexpected key", 1, ((MOONumber)firstCanon).getValue());
        MOOValue firstPrint = ((MOOList)first).getValue().get(2);
        Assert.assertTrue("Should contain a number", firstPrint instanceof MOONumber);
        Assert.assertEquals("Unexpected firstPrint", true, ((MOONumber)firstPrint).toBoolean());
    }
}
