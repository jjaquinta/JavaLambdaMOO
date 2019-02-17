package com.tsatsatzu.moo.test.api;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;
import com.tsatsatzu.moo.test.MinimalDBBase;

public class PlayerAPITest extends MinimalDBBase
{
    @Test
    public void testPlayers() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "players();");
        Assert.assertTrue("Script should return number", ret instanceof MOOList);
        MOOList players = (MOOList)ret;
        Assert.assertEquals("Should be 1 player", 1, players.size());
        MOOValue first = players.get(0);
        Assert.assertTrue("List should contain numbers", first instanceof MOONumber);
        Assert.assertEquals("Player should be #3", 3, ((MOONumber)first).getValue());
    }

    @Test
    public void testIsPlayer() throws MOOException
    {
        MOOValue ret1 = MOOScriptLogic.executeScript(mProgrammer, "is_player('#3');");
        Assert.assertTrue("Script should return number", ret1 instanceof MOONumber);
        Assert.assertNotEquals("Should not be false", 0, ((MOONumber)ret1).getValue());
    }

    @Test
    public void testIsNotPlayer() throws MOOException
    {
        MOOValue ret1 = MOOScriptLogic.executeScript(mProgrammer, "is_player('#1');");
        Assert.assertTrue("Script should return number", ret1 instanceof MOONumber);
        Assert.assertEquals("Should be false", 0, ((MOONumber)ret1).getValue());
    }

    @Test
    public void testSetPlayer() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "set_player_flag('#3', true);");
        Assert.assertNull("Empty script should return null", ret);
    }

}
