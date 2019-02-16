package com.tsatsatzu.moo.test.api;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;
import com.tsatsatzu.moo.test.MinimalDBBase;

public class VerbAPITest extends MinimalDBBase
{
    @Test
    public void testVerbs() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "verbs('#0');");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        MOOList verbs = (MOOList)ret;
        Assert.assertEquals("Should be one verb", 1, verbs.getValue().size());
        MOOValue verb = verbs.getValue().get(0);
        Assert.assertTrue("List should contain string", verb instanceof MOOString);
        Assert.assertEquals("Unexpected verb", "do_login_command", ((MOOString)verb).getValue());
    }

    @Test
    public void testVerbInfo() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "verb_info('#0', 'do_login_command');");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        MOOList info = (MOOList)ret;
        Assert.assertEquals("Should be 3 infos", 3, info.getValue().size());
        MOOValue owner = info.getValue().get(0);
        MOOValue perms = info.getValue().get(1);
        MOOValue names = info.getValue().get(2);
        Assert.assertTrue("First info item should be Object", owner instanceof MOOObjRef);
        Assert.assertTrue("Second info item should be String", perms instanceof MOOString);
        Assert.assertTrue("Third info item should be String", names instanceof MOOString);
        Assert.assertEquals("Unexpected owner", 3, ((MOOObjRef)owner).getValue());
        Assert.assertEquals("Unexpected perms", "rx", ((MOOString)perms).getValue());
        Assert.assertEquals("Unexpected name", "do_login_command", ((MOOString)names).getValue());
    }

    @Test
    public void testSetVerbInfo() throws MOOException
    {
        MOOScriptLogic.executeScript(mProgrammer, "var info = []; info[0] = toobj('#2'); info[1] = 'rw'; info[2] = 'wibble'; set_verb_info('#0', 'do_login_command', info);");
        MOOObject obj = MOODbLogic.get(0);
        MOOVerb verb = obj.getVerb("wibble");
        Assert.assertNotNull("Couldn't find verb 'wibble'", verb);
        Assert.assertTrue("Should be read", verb.isRead());
        Assert.assertTrue("Should be write", verb.isWrite());
        Assert.assertFalse("Should not be execute", verb.isExecute());
        Assert.assertEquals("Unexpected name", "wibble", verb.getName());
    }

    @Test
    public void testVerbArgs() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "verb_args('#0', 'do_login_command');");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        MOOList info = (MOOList)ret;
        Assert.assertEquals("Should be 3 infos", 3, info.getValue().size());
        MOOValue dobj = info.getValue().get(0);
        MOOValue prep = info.getValue().get(1);
        MOOValue iobj = info.getValue().get(2);
        Assert.assertTrue("First info item should be String", dobj instanceof MOOString);
        Assert.assertTrue("Second info item should be String", prep instanceof MOOString);
        Assert.assertTrue("Third info item should be String", iobj instanceof MOOString);
        Assert.assertEquals("Unexpected direct object", "this", ((MOOString)dobj).getValue());
        Assert.assertEquals("Unexpected preposition", "n/a", ((MOOString)prep).getValue());
        Assert.assertEquals("Unexpected indirect object", "this", ((MOOString)iobj).getValue());
    }

    @Test
    public void testSetVerbArgs() throws MOOException
    {
        MOOScriptLogic.executeScript(mProgrammer, "var args = []; args[0] = 'any'; args[1] = 'over'; args[2] = 'any'; set_verb_args('#0', 'do_login_command', args);");
        MOOObject obj = MOODbLogic.get(0);
        MOOVerb verb = obj.getVerb("do_login_command");
        Assert.assertNotNull("Couldn't find verb 'do_login_command'", verb);
        Assert.assertEquals("Should be any", MOOVerb.DO_ANY, verb.getDirectObjectType());
        Assert.assertEquals("Should be write", MOOVerb.PREP_OVER, verb.getPrepositionType());
        Assert.assertEquals("Should be any", MOOVerb.IO_ANY, verb.getIndirectObjectType());
    }

    @Test
    public void testAddVerb() throws MOOException
    {
        MOOScriptLogic.executeScript(mProgrammer, 
                "var info = []; info[0] = toobj('#2'); info[1] = 'rw'; info[2] = 'wibble'; "
                + "var args = []; args[0] = 'any'; args[1] = 'over'; args[2] = 'any'; "
                + "addVerb('#0', info, args);");
        MOOObject obj = MOODbLogic.get(0);
        MOOVerb verb = obj.getVerb("wibble");
        Assert.assertNotNull("Couldn't find verb 'wibble'", verb);
        Assert.assertEquals("Should be any", MOOVerb.DO_ANY, verb.getDirectObjectType());
        Assert.assertEquals("Should be write", MOOVerb.PREP_OVER, verb.getPrepositionType());
        Assert.assertEquals("Should be any", MOOVerb.IO_ANY, verb.getIndirectObjectType());
        Assert.assertTrue("Should be read", verb.isRead());
        Assert.assertTrue("Should be write", verb.isWrite());
        Assert.assertFalse("Should not be execute", verb.isExecute());
        Assert.assertEquals("Unexpected name", "wibble", verb.getName());
    }

    @Test
    public void testDeleteVerb() throws MOOException
    {
        MOOScriptLogic.executeScript(mProgrammer, "delete_verb('#0', 'do_login_command');");
        MOOObject obj = MOODbLogic.get(0);
        MOOVerb verb = obj.getVerb("do_login_command");
        Assert.assertNull("Verb not deleted", verb);
    }

    @Test
    public void testVerbCode() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "verb_code('#0', 'do_login_command', true, true);");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        MOOList code = (MOOList)ret;
        Assert.assertEquals("Should be one line of code", 1, code.getValue().size());
        MOOValue line = code.getValue().get(0);
        Assert.assertTrue("List should contain string", line instanceof MOOString);
        Assert.assertEquals("Unexpected code", "toobj('#3');", ((MOOString)line).getValue());
    }

    @Test
    public void testSetVerbCode() throws MOOException
    {
        MOOValue ret = MOOScriptLogic.executeScript(mProgrammer, "var code = []; code[0] = \"return '#2';\"; set_verb_code('#0', 'do_login_command', code);");
        Assert.assertTrue("Script should return List", ret instanceof MOOList);
        MOOList err = (MOOList)ret;
        Assert.assertEquals("Should be no errors", 0, err.getValue().size());
        MOOObject obj = MOODbLogic.get(0);
        MOOVerb verb = obj.getVerb("do_login_command");
        Assert.assertNotNull("Couldn't find verb 'do_login_command'", verb);
        Assert.assertEquals("Should be one line of code", 1, verb.getScript().size());
        Assert.assertEquals("Unexpected code", "return '#2';", verb.getScript().get(0));
    }

    @Test
    public void testInlineCalling() throws MOOException
    {
        MOOString name = new MOOString("test1");
        MOOObjRef room = new MOOObjRef(2);
        MOOList info = new MOOList();
        info.getValue().add(new MOOObjRef(3));
        info.getValue().add(new MOOString("rwx"));
        info.getValue().add(name);
        MOOList args = new MOOList();
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(room, info, args);
        MOOList code = new MOOList();
        code.getValue().add(new MOOString("3;"));
        MOOVerbAPI.set_verb_code(room, name, code);
        MOOValue val1 = MOOScriptLogic.executeScript(mProgrammer, "toObj('#2').test1();");
        Assert.assertTrue("Should return a number", val1 instanceof MOONumber);
        Assert.assertEquals("Should return 3", 3, ((MOONumber)val1).getValue());
        MOOValue val2 = MOOScriptLogic.executeScript(mProgrammer, "toObj('#3').location.test1();");
        Assert.assertTrue("Should return a number", val2 instanceof MOONumber);
        Assert.assertEquals("Should return 3", 3, ((MOONumber)val2).getValue());
    }

    @Test
    public void testInlineCrossCalling() throws MOOException
    {
        MOOString name1 = new MOOString("test1");
        MOOString name2 = new MOOString("test2");
        MOOObjRef room = new MOOObjRef(2);
        MOOList info1 = new MOOList();
        info1.getValue().add(new MOOObjRef(3));
        info1.getValue().add(new MOOString("rwx"));
        info1.getValue().add(name1);
        MOOList info2 = new MOOList();
        info2.getValue().add(new MOOObjRef(3));
        info2.getValue().add(new MOOString("rwx"));
        info2.getValue().add(name2);
        MOOList args = new MOOList();
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        MOOList code1 = new MOOList();
        code1.getValue().add(new MOOString("3;"));
        MOOList code2 = new MOOList();
        code2.getValue().add(new MOOString("_this.test1() + 1;"));
        MOOVerbAPI.add_verb(room, info1, args);
        MOOVerbAPI.set_verb_code(room, name1, code1);
        MOOVerbAPI.add_verb(room, info2, args);
        MOOVerbAPI.set_verb_code(room, name2, code2);
        MOOValue val1 = MOOScriptLogic.executeScript(mProgrammer, "toObj('#2').test2();");
        Assert.assertTrue("Should return a number", val1 instanceof MOONumber);
        Assert.assertEquals("Should return 4", 4.0, ((MOONumber)val1).getValue());
    }
}
