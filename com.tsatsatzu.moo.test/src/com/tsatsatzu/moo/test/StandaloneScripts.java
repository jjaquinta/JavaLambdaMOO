package com.tsatsatzu.moo.test;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

public class StandaloneScripts extends MinimalDBBase
{
    @Test
    public void emptyScript() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript("");
        Assert.assertNull("Empty script should return null", ret);
    }

    @Test
    public void simpleScript() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript("var x; x = 1 + 2;");
        Assert.assertTrue("Script should return number", ret instanceof MOONumber);
        Assert.assertEquals("Script should return number 3", 3, ((MOONumber)ret).getValue());
    }

    @Test
    public void simpleScriptWithReturn() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript("var x; x = 1 + 2; x;");
        Assert.assertTrue("Script should return number", ret instanceof MOONumber);
        Assert.assertEquals("Script should return number 3", 3, ((MOONumber)ret).getValue());
    }

}
