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
        info.getValue().add(new MOOObjRef(3));
        info.getValue().add(new MOOString("rwx"));
        info.getValue().add(name);
        MOOList args = new MOOList();
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(room, info, args);
        MOOList code = new MOOList();
        code.getValue().add(new MOOString("notify(player, \"WTF?\\n\", false);"));
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
        info.getValue().add(new MOOObjRef(3));
        info.getValue().add(new MOOString("rwx"));
        info.getValue().add(name);
        MOOList args = new MOOList();
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(obj, info, args);
        MOOList code = new MOOList();
        code.getValue().add(new MOOString("notify(player, \"wobble\\n\", false);"));
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
        info.getValue().add(new MOOObjRef(3));
        info.getValue().add(new MOOString("rwx"));
        info.getValue().add(name);
        MOOList args = new MOOList();
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        args.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(obj, info, args);
        MOOList code = new MOOList();
        code.getValue().add(new MOOString("notify(player, \"wobble\\n\", false);"));
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
        info1.getValue().add(new MOOObjRef(3));
        info1.getValue().add(new MOOString("rwx"));
        info1.getValue().add(name1);
        MOOList args1 = new MOOList();
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(room, info1, args1);
        MOOList code1 = new MOOList();
        code1.getValue().add(new MOOString("true"));
        MOOVerbAPI.set_verb_code(room, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, room);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.getValue().add(new MOOObjRef(3));
        info2.getValue().add(new MOOString("rwx"));
        info2.getValue().add(name2);
        MOOList args2 = new MOOList();
        args2.getValue().add(new MOOString("this"));
        args2.getValue().add(new MOOString("none"));
        args2.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.getValue().add(new MOOString("notify(player, \"wobble\\n\", false);"));
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
        info1.getValue().add(new MOOObjRef(3));
        info1.getValue().add(new MOOString("rwx"));
        info1.getValue().add(name1);
        MOOList args1 = new MOOList();
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(mProgrammer, info1, args1);
        MOOList code1 = new MOOList();
        code1.getValue().add(new MOOString("true"));
        MOOVerbAPI.set_verb_code(mProgrammer, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, mProgrammer);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.getValue().add(new MOOObjRef(3));
        info2.getValue().add(new MOOString("rwx"));
        info2.getValue().add(name2);
        MOOList args2 = new MOOList();
        args2.getValue().add(new MOOString("this"));
        args2.getValue().add(new MOOString("none"));
        args2.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.getValue().add(new MOOString("notify(player, \"wobble\\n\", false);"));
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
        info1.getValue().add(new MOOObjRef(3));
        info1.getValue().add(new MOOString("rwx"));
        info1.getValue().add(name1);
        MOOList args1 = new MOOList();
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(room, info1, args1);
        MOOList code1 = new MOOList();
        code1.getValue().add(new MOOString("true"));
        MOOVerbAPI.set_verb_code(room, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, room);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.getValue().add(new MOOObjRef(3));
        info2.getValue().add(new MOOString("rwx"));
        info2.getValue().add(name2);
        MOOList args2 = new MOOList();
        args2.getValue().add(new MOOString("none"));
        args2.getValue().add(new MOOString("over"));
        args2.getValue().add(new MOOString("this"));
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.getValue().add(new MOOString("notify(player, \"wobble\\n\", false);"));
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
        info1.getValue().add(new MOOObjRef(3));
        info1.getValue().add(new MOOString("rwx"));
        info1.getValue().add(name1);
        MOOList args1 = new MOOList();
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        args1.getValue().add(new MOOString("none"));
        MOOVerbAPI.add_verb(mProgrammer, info1, args1);
        MOOList code1 = new MOOList();
        code1.getValue().add(new MOOString("true"));
        MOOVerbAPI.set_verb_code(mProgrammer, name1, code1);

        MOOObjRef thing = MOOObjectAPI.create(new MOOObjRef(1), mProgrammer);
        MOOObjectAPI.setPropertyValue(MOODbLogic.get(thing), "name", new MOOString("gronk"));
        MOOMoveAPI.move(thing, mProgrammer);
        
        MOOString name2 = new MOOString("wibble");
        MOOList info2 = new MOOList();
        info2.getValue().add(new MOOObjRef(3));
        info2.getValue().add(new MOOString("rwx"));
        info2.getValue().add(name2);
        MOOList args2 = new MOOList();
        args2.getValue().add(new MOOString("none"));
        args2.getValue().add(new MOOString("over"));
        args2.getValue().add(new MOOString("this"));
        MOOVerbAPI.add_verb(thing, info2, args2);
        MOOList code2 = new MOOList();
        code2.getValue().add(new MOOString("notify(player, \"wobble\\n\", false);"));
        MOOVerbAPI.set_verb_code(thing, name2, code2);

        connect();
        writeLine("wibble over gronk");
        String reply = readLine();
        Assert.assertEquals("Should see print", "wobble", reply);
        disconnect();
    }
}
