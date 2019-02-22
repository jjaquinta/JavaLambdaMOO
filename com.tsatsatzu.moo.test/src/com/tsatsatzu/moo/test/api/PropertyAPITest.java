package com.tsatsatzu.moo.test.api;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.api.MOOPropertyAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOProperty;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;
import com.tsatsatzu.moo.test.MinimalDBBase;

public class PropertyAPITest extends MinimalDBBase
{
    @Test
    public void testProperties() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript("properties(#0);");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        MOOList props = (MOOList)ret;
        Assert.assertEquals("Should be no defined properties", 0, props.size());
    }

    @Test
    public void testPropertyInfo() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(2), new MOOString("temperature"), new MOONumber(32), info);
        MOOValue ret = MOOScriptLogic.executeScript("property_info(#2, 'temperature');");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        info = (MOOList)ret;
        Assert.assertEquals("Should be 3 infos", 2, info.size());
        MOOValue owner = info.get(0);
        MOOValue perms = info.get(1);
        Assert.assertTrue("First info item should be Object", owner instanceof MOOObjRef);
        Assert.assertTrue("Second info item should be String", perms instanceof MOOString);
        Assert.assertEquals("Unexpected owner", 3, ((MOOObjRef)owner).getValue());
        Assert.assertEquals("Unexpected perms", "rw", ((MOOString)perms).getValue());
    }

    @Test
    public void testSetPropertyInfo() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(2), new MOOString("temperature"), new MOONumber(32), info);
        MOOScriptLogic.executeScript("var info = []; info[0] = toobj(#0); info[1] = 'rwc'; info[2] = 'wibble'; set_property_info(#2, 'temperature', info);");
        MOOObject obj = MOODbLogic.get(2);
        MOOProperty prop = obj.getProperties().get("wibble");
        Assert.assertNotNull("Couldn't find property 'wibble'", prop);
        Assert.assertEquals("Should be owner zero", 0, prop.getOwner().getValue());
        Assert.assertTrue("Should be read", prop.isRead());
        Assert.assertTrue("Should be write", prop.isWrite());
        Assert.assertTrue("Should be change", prop.isChange());
        Assert.assertEquals("Unexpected name", "wibble", prop.getName());
    }

    @Test
    public void testAddProperty() throws MOOException
    {
        MOOScriptLogic.executeScript(
                "var info = []; info[0] = toobj(#3); info[1] = 'rw'; "
                + "add_property(#2, 'wibble', 32, info);");
        MOOObject obj = MOODbLogic.get(2);
        MOOProperty prop = obj.getProperties().get("wibble");
        Assert.assertNotNull("Couldn't find property 'wibble'", prop);
        Assert.assertEquals("Should be owner three", 3, prop.getOwner().getValue());
        Assert.assertTrue("Should be read", prop.isRead());
        Assert.assertTrue("Should be write", prop.isWrite());
        Assert.assertFalse("Should not be change", prop.isChange());
        Assert.assertEquals("Unexpected name", "wibble", prop.getName());
    }

    @Test
    public void testDeleteProperty() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(2), new MOOString("temperature"), new MOONumber(32), info);
        MOOScriptLogic.executeScript("delete_property(#2, 'temperature');");
        MOOObject obj = MOODbLogic.get(0);
        MOOProperty prop = obj.getProperties().get("wibble");
        Assert.assertNull("Property not deleted", prop);
    }

    @Test
    public void testIsClearProperty() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(1), new MOOString("temperature"), new MOONumber(32), info);
        MOOValue ret1 = MOOScriptLogic.executeScript("is_clear_property(#1, 'temperature');");
        Assert.assertTrue("Script should return Number", ret1 instanceof MOONumber);
        MOONumber isClear1 = (MOONumber)ret1;
        Assert.assertEquals("Should not be clear", 0, isClear1.getValue());
        MOOValue ret2 = MOOScriptLogic.executeScript("is_clear_property(#2, 'temperature');");
        Assert.assertTrue("Script should return Number", ret2 instanceof MOONumber);
        MOONumber isClear2 = (MOONumber)ret2;
        Assert.assertNotEquals("Should be clear", 0, isClear2.getValue());
    }

    @Test
    public void testClearProperty() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(1), new MOOString("temperature"), new MOONumber(32), info);
        MOOPropertyAPI.set_property(new MOOObjRef(2), new MOOString("temperature"), new MOONumber(16));
        MOOValue ret2 = MOOScriptLogic.executeScript("is_clear_property(#2, 'temperature');");
        Assert.assertTrue("Script should return Number", ret2 instanceof MOONumber);
        MOONumber isClear2 = (MOONumber)ret2;
        Assert.assertEquals("Should not be clear", 0, isClear2.getValue());
        MOOScriptLogic.executeScript("clear_property(#2, 'temperature');");
        MOOValue ret1 = MOOScriptLogic.executeScript("is_clear_property(#2, 'temperature');");
        Assert.assertTrue("Script should return Number", ret1 instanceof MOONumber);
        MOONumber isClear1 = (MOONumber)ret1;
        Assert.assertEquals("Should be clear", 1, isClear1.getValue());
    }

    @Test
    public void testSetProperty() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(1), new MOOString("temperature"), new MOONumber(32), info);
        MOOScriptLogic.executeScript("set_property(#2, 'temperature', 16);");
        MOOValue val = MOOPropertyAPI.get_property(new MOOObjRef(2), new MOOString("temperature"));
        Assert.assertTrue("Value should be Number", val instanceof MOONumber);
        MOONumber temperature = (MOONumber)val;
        Assert.assertEquals("Should be 16", 16, temperature.getValue());
    }

    @Test
    public void testGetProperty() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(1), new MOOString("temperature"), new MOONumber(32), info);
        MOOValue val = MOOScriptLogic.executeScript("get_property(#2, 'temperature');");
        Assert.assertTrue("Value should be Number", val instanceof MOONumber);
        MOONumber temperature = (MOONumber)val;
        Assert.assertEquals("Should be 32", 32, temperature.getValue());
    }

    @Test
    public void testInlineProperties() throws MOOException
    {
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rw");
        MOOPropertyAPI.add_property(new MOOObjRef(1), new MOOString("temperature"), new MOONumber(32), info);
        MOOValue val1 = MOOScriptLogic.executeScript("toobj(#2).temperature;");
        Assert.assertTrue("Value should be Number", val1 instanceof MOONumber);
        Assert.assertEquals("Should be 32", 32, ((MOONumber)val1).getValue());
        MOOValue val2 = MOOScriptLogic.executeScript("var obj = toobj(#2); obj.temperature = 16; obj.temperature;");
        Assert.assertTrue("Value should be Number", val2 instanceof MOONumber);
        Assert.assertEquals("Should be 16", 16, ((MOONumber)val2).getValue());
    }
}
