package com.tsatsatzu.moo.test.api;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.api.MOOMatchAPI;
import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.api.MOOPropertyAPI;
import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOMap;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.test.MinimalDBBase;

public class MatchAPITest extends MinimalDBBase
{
    @Test
    public void testExact() throws MOOException
    {
        MOOMap match = MOOMatchAPI.match_word(new MOOString("hello"), new MOOString("hello"));
        Assert.assertTrue("Expected confidence", match.get("confidence") instanceof MOONumber);
        Assert.assertEquals("Should be confident", 0.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
    }
    @Test
    public void testInexact() throws MOOException
    {
        MOOMap match = MOOMatchAPI.match_word(new MOOString("hello"), new MOOString("help"));
        Assert.assertTrue("Expected confidence", match.get("confidence") instanceof MOONumber);
        Assert.assertEquals("Should be semi-confident", 2.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
    }
    @Test
    public void testUnequal() throws MOOException
    {
        MOOMap match = MOOMatchAPI.match_word(new MOOString("hello"), new MOOString("artichoke"));
        Assert.assertTrue("Expected confidence", match.get("confidence") instanceof MOONumber);
        Assert.assertEquals("Should be semi-confident", 5.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
    }
    @Test
    public void testObject() throws MOOException
    {
        MOOObjRef root = new MOOObjRef(1);
        MOOObjRef obj1 = MOOObjectAPI.create(root, mProgrammer);
        MOOPropertyAPI.set_property(obj1, new MOOString("name"), new MOOString("blue"));
        MOOObjRef obj2 = MOOObjectAPI.create(root, mProgrammer);
        MOOPropertyAPI.set_property(obj2, new MOOString("name"), new MOOString("red"));
        MOOList objs = new MOOList();
        objs.add(obj1);
        objs.add(obj2);
        MOOList matches = MOOMatchAPI.match_list(new MOOString("blew"), objs, new MOONumber(1));
        Assert.assertEquals("Expected single return", 1, matches.size());
        Assert.assertTrue("Expected map", matches.get(0) instanceof MOOMap);
        MOOMap match = (MOOMap)matches.get(0);
        Assert.assertEquals("Should be confident", 0.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
        Assert.assertEquals("Should be obj1", obj1.getValue(), ((MOOObjRef)match.get("matchedObject")).getValue());
        matches = MOOMatchAPI.match_list(new MOOString("rude"), objs, new MOONumber(1));
        Assert.assertEquals("Expected single return", 1, matches.size());
        Assert.assertTrue("Expected map", matches.get(0) instanceof MOOMap);
        match = (MOOMap)matches.get(0);
        Assert.assertEquals("Should be confident", 1.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
        Assert.assertEquals("Should be obj2", obj2.getValue(), ((MOOObjRef)match.get("matchedObject")).getValue());
    }
    @Test
    public void testVerb() throws MOOException
    {
        MOOObjRef root = new MOOObjRef(1);
        MOOObjRef obj = MOOObjectAPI.create(root, mProgrammer);
        MOOList info1 = new MOOList(mProgrammer, "rw", "blue");
        MOOList args = new MOOList("none", "none", "none");
        MOOVerbAPI.add_verb(obj, info1, args);
        MOOList info2 = new MOOList(mProgrammer, "rw", "red");
        MOOVerbAPI.add_verb(obj, info2, args);
        MOOList objs = new MOOList(obj);

        MOOList matches = MOOMatchAPI.match_verbs(new MOOString("blew"), objs, new MOONumber(1));
        Assert.assertEquals("Expected single return", 1, matches.size());
        Assert.assertTrue("Expected map", matches.get(0) instanceof MOOMap);
        MOOMap match = (MOOMap)matches.get(0);
        Assert.assertEquals("Should be confident", 0.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
        Assert.assertEquals("Should be obj", obj.getValue(), ((MOOObjRef)match.get("matchedObject")).getValue());
        Assert.assertEquals("Should be blue", "blue", ((MOOString)match.get("matchedVerb")).getValue());
        matches = MOOMatchAPI.match_verbs(new MOOString("rude"), objs, new MOONumber(1));
        Assert.assertEquals("Expected single return", 1, matches.size());
        Assert.assertTrue("Expected map", matches.get(0) instanceof MOOMap);
        match = (MOOMap)matches.get(0);
        Assert.assertEquals("Should be confident", 1.0, ((MOONumber)match.get("confidence")).getValue().floatValue(), 0.001);
        Assert.assertEquals("Should be obj", obj.getValue(), ((MOOObjRef)match.get("matchedObject")).getValue());
        Assert.assertEquals("Should be red", "red", ((MOOString)match.get("matchedVerb")).getValue());
    }
}
