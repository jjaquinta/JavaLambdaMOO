package com.tsatsatzu.moo.test.api;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;
import com.tsatsatzu.moo.test.MinimalDBBase;

public class ObjectAPITest extends MinimalDBBase
{
    @Test
    public void testCreate() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "create(#2, #3);");
        Assert.assertTrue("Script should return object", ret instanceof MOOObjRef);
        MOOObjRef newObjRef = (MOOObjRef)ret;
        Assert.assertEquals("Should next object", 4, newObjRef.getValue());
        MOOObject newObj = MOODbLogic.get(newObjRef);
        Assert.assertEquals("Unexpected parent", 2, newObj.getParent().intValue());
        Assert.assertEquals("Unexpected owner", 3, newObj.getOwner().getValue());
    }
    @Test
    public void testChParent() throws MOOException
    {
        MOOObjRef firstRoom = new MOOObjRef(2);
        MOOObjRef secondRoom = MOOObjectAPI.create(firstRoom, new MOOObjRef(mProgrammer));
        MOOScriptLogic.executeScript(mProgrammer, "chparent(#4, #3);");
        MOOObject newObj = MOODbLogic.get(secondRoom);
        Assert.assertEquals("Unexpected parent", 3, newObj.getParent().intValue());
    }
    @Test
    public void testValid() throws MOOException
    {
        MOOValue ret1 = MOOScriptLogic.executeScript(mProgrammer, "valid(#3);");
        Assert.assertTrue("Script should return number", ret1 instanceof MOONumber);
        Assert.assertTrue("Script should return true", ((MOONumber)ret1).toBoolean());
        MOOValue ret2 = MOOScriptLogic.executeScript(mProgrammer, "valid(#4);");
        Assert.assertTrue("Script should return number", ret2 instanceof MOONumber);
        Assert.assertFalse("Script should return false", ((MOONumber)ret2).toBoolean());
    }
    @Test
    public void testParent() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "parent(#3);");
        Assert.assertTrue("Script should return number", ret instanceof MOOObjRef);
        Assert.assertEquals("Script should return true", 1, ((MOOObjRef)ret).getValue());
    }
    @Test
    public void testChildren() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "children(#1);");
        Assert.assertTrue("Script should return list", ret instanceof MOOList);
        Assert.assertEquals("Script should return three items in list", 3, ((MOOList)ret).size());
    }
    @Test
    public void testRecycle() throws MOOException
    {
        Assert.assertNotNull("Object should exist", MOODbLogic.get(3));
        MOOScriptLogic.executeScript(mProgrammer, "recycle(#3);");
        Assert.assertNull("Object should no longer exist", MOODbLogic.get(3));
    }
    @Test
    public void testPassAround() throws MOOException
    {
        MOOScriptLogic.executeScript(mProgrammer, "var x = create(#2, #3); recycle(x);");
        Assert.assertNull("Object should be gone", MOODbLogic.get(4));
    }
    @Test
    public void testMove() throws MOOException
    {
        MOOObjRef firstRoom = new MOOObjRef(2);
        MOOString name = new MOOString("accept");
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rwx");
        info.add(name);
        MOOList args = new MOOList();
        args.add("none");
        args.add("none");
        args.add("none");
        MOOVerbAPI.add_verb(firstRoom, info, args);
        MOOList code = new MOOList();
        code.add("true;");
        MOOVerbAPI.set_verb_code(firstRoom, name, code);
        MOOObjectAPI.create(firstRoom, new MOOObjRef(mProgrammer));
        MOOScriptLogic.executeScript(mProgrammer, "move(#3, #4);");
        MOOObject player = MOODbLogic.get(3);
        Assert.assertEquals("Should be in new room", 4, player.getLocation().getValue());
    }
}
