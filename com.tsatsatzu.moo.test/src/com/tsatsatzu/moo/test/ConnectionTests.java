package com.tsatsatzu.moo.test;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.core.api.MOOMoveAPI;
import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;

public class ConnectionTests extends MinimalTelnetBase
{
    @Test
    public void connectTest() throws Exception
    {
        connect();
        writeLine("test");
        String huh = readLine();
        Assert.assertEquals("Should be no such command text", "I didn't understand that.", huh);
        disconnect();
    }
    @Test
    public void huhVerb() throws Exception
    {
        MOOString name = new MOOString("huh");
        MOOObjRef room = new MOOObjRef(2);
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rwx");
        info.add(name);
        MOOList args = new MOOList();
        args.add("none");
        args.add("none");
        args.add("none");
        MOOVerbAPI.add_verb(room, info, args);
        MOOList code = new MOOList();
        code.add("notify(_player, \"WTF?\\n\", false);");
        MOOVerbAPI.set_verb_code(room, name, code);

        connect();
        writeLine("test");
        String huh = readLine();
        Assert.assertEquals("Should be no such command text", "WTF?", huh);
        disconnect();
    }
    @Test
    public void playerVerb() throws Exception
    {
        MOOString name = new MOOString("wibble");
        MOOObjRef obj = new MOOObjRef(3);
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rwx");
        info.add(name);
        MOOList args = new MOOList();
        args.add("none");
        args.add("none");
        args.add("none");
        MOOVerbAPI.add_verb(obj, info, args);
        MOOList code = new MOOList();
        code.add("notify(_player, \"wobble\\n\", false);");
        MOOVerbAPI.set_verb_code(obj, name, code);

        connect();
        writeLine("wibble");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
    @Test
    public void locationVerb() throws Exception
    {
        MOOString name = new MOOString("wibble");
        MOOObjRef obj = new MOOObjRef(2);
        MOOList info = new MOOList();
        info.add(new MOOObjRef(3));
        info.add("rwx");
        info.add(name);
        MOOList args = new MOOList();
        args.add("none");
        args.add("none");
        args.add("none");
        MOOVerbAPI.add_verb(obj, info, args);
        MOOList code = new MOOList();
        code.add("notify(_player, \"wobble\\n\", false);");
        MOOVerbAPI.set_verb_code(obj, name, code);

        connect();
        writeLine("wibble");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
    @Test
    public void directObjectLocationVerb() throws Exception
    {
        MOOObjRef room = new MOOObjRef(2);
        MOOString name1 = new MOOString("accept");
        MOOList info1 = new MOOList();
        info1.add(new MOOObjRef(3));
        info1.add("rwx");
        info1.add(name1);
        MOOList args1 = new MOOList();
        args1.add("none");
        args1.add("none");
        args1.add("none");
        MOOVerbAPI.add_verb(room, info1, args1);
        MOOList code1 = new MOOList();
        code1.add("true");
        MOOVerbAPI.set_verb_code(room, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, room);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.add(new MOOObjRef(3));
        info2.add("rwx");
        info2.add(name2);
        MOOList args2 = new MOOList();
        args2.add("this");
        args2.add("none");
        args2.add("none");
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.add("notify(_player, \"wobble\\n\", false);");
        MOOVerbAPI.set_verb_code(thing, name2, code2);

        connect();
        writeLine("wibble gronk");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
    @Test
    public void directObjectPlayerVerb() throws Exception
    {
        MOOString name1 = new MOOString("accept");
        MOOList info1 = new MOOList();
        info1.add(new MOOObjRef(3));
        info1.add("rwx");
        info1.add(name1);
        MOOList args1 = new MOOList();
        args1.add("none");
        args1.add("none");
        args1.add("none");
        MOOVerbAPI.add_verb(mProgrammer, info1, args1);
        MOOList code1 = new MOOList();
        code1.add("true");
        MOOVerbAPI.set_verb_code(mProgrammer, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, mProgrammer);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.add(new MOOObjRef(3));
        info2.add("rwx");
        info2.add(name2);
        MOOList args2 = new MOOList();
        args2.add("this");
        args2.add("none");
        args2.add("none");
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.add("notify(_player, \"wobble\\n\", false);");
        MOOVerbAPI.set_verb_code(thing, name2, code2);

        connect();
        writeLine("wibble gronk");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
    @Test
    public void indirectObjectLocationVerb() throws Exception
    {
        MOOObjRef room = new MOOObjRef(2);
        MOOString name1 = new MOOString("accept");
        MOOList info1 = new MOOList();
        info1.add(new MOOObjRef(3));
        info1.add("rwx");
        info1.add(name1);
        MOOList args1 = new MOOList();
        args1.add("none");
        args1.add("none");
        args1.add("none");
        MOOVerbAPI.add_verb(room, info1, args1);
        MOOList code1 = new MOOList();
        code1.add("true");
        MOOVerbAPI.set_verb_code(room, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, room);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.add(new MOOObjRef(3));
        info2.add("rwx");
        info2.add(name2);
        MOOList args2 = new MOOList();
        args2.add("none");
        args2.add("over");
        args2.add("this");
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.add("notify(_player, \"wobble\\n\", false);");
        MOOVerbAPI.set_verb_code(thing, name2, code2);

        connect();
        writeLine("wibble over gronk");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
    @Test
    public void indirectObjectPlayerVerb() throws Exception
    {
        MOOString name1 = new MOOString("accept");
        MOOList info1 = new MOOList();
        info1.add(new MOOObjRef(3));
        info1.add("rwx");
        info1.add(name1);
        MOOList args1 = new MOOList();
        args1.add("none");
        args1.add("none");
        args1.add("none");
        MOOVerbAPI.add_verb(mProgrammer, info1, args1);
        MOOList code1 = new MOOList();
        code1.add("true");
        MOOVerbAPI.set_verb_code(mProgrammer, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, mProgrammer);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.add(new MOOObjRef(3));
        info2.add("rwx");
        info2.add(name2);
        MOOList args2 = new MOOList();
        args2.add("none");
        args2.add("over");
        args2.add("this");
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.add("notify(_player, \"wobble\\n\", false);");
        MOOVerbAPI.set_verb_code(thing, name2, code2);

        connect();
        writeLine("wibble over gronk");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
}
