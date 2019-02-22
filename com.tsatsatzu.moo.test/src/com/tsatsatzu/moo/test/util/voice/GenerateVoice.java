package com.tsatsatzu.moo.test.util.voice;

import java.io.File;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.api.MOOPropertyAPI;
import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class GenerateVoice
{
    private MOOObject mSystem;
    private MOOObject mRoot;
    private MOOObject mRoom;
    private MOOObject mThing;
    private MOOObject mContainer;
    private MOOObject mPlayer;
    private MOOObject mProgrammer;
    private MOOObject mWizard;
    private MOOObject mGod;
    private MOOObject mEntryRoom;
    
    private void setup() throws InterruptedException
    {
        File coreFile = new File("C:\\Users\\JoGrant\\git\\JavaLambdaMOO\\com.tsatsatzu.moo.core\\dbs\\VoiceCore.jdb");
        if (coreFile.exists())
            coreFile.delete();
        System.setProperty("moo.store.class", "com.tsatsatzu.moo.test.util.MinimalJDBStore");
        System.setProperty("moo.store.mem.disk", "C:\\Users\\JoGrant\\git\\JavaLambdaMOO\\com.tsatsatzu.moo.core\\dbs\\VoiceCore.jdb");
        MOOOpsLogic.initailize();
    }
    
    private void setdown() throws Exception
    {
        MOOProgrammerLogic.pushProgrammer(mWizard.toRef());
        MOOOpsLogic.shutdown();
    }
    
    private void addGlobalConstant(String propName, MOOValue value) throws MOOException
    {
        MOOList info = new MOOList();
        info.add(mGod.toRef());
        info.add("r");
        MOOPropertyAPI.add_property(mSystem.toRef(), new MOOString(propName), value, info);
    }
    
    private void addGlobalConstant(String propName, MOOObject obj) throws MOOException
    {
        addGlobalConstant(propName, obj.toRef());
    }
    
    private void createBaseObjectHierarchy() throws MOOException
    {
        // bootstrap objects
        mSystem = MOODbLogic.get(0);
        mRoot = MOODbLogic.get(1);
        mRoom = MOODbLogic.get(2);
        mGod = MOODbLogic.get(3);
        MOOProgrammerLogic.pushProgrammer(mGod.toRef());
        // create objects
        mThing = MOODbLogic.newInstance(-1);
        mContainer = MOODbLogic.newInstance(-1);
        mPlayer = MOODbLogic.newInstance(-1);
        mProgrammer = MOODbLogic.newInstance(-1);
        mWizard = MOODbLogic.newInstance(-1);
        mEntryRoom = MOODbLogic.newInstance(mRoom.getOID());
        // system details
        mSystem.setName("System Object");
        // root
        mRoot.setName("The Root Class");
        addGlobalConstant("root_class", mRoot);
        // room
        mRoom.setName("The Generic Room");
        addGlobalConstant("room", mRoom);
        // thing
        mThing.setName("The Generic Thing");
        MOOObjectAPI.chparent(mThing.toRef(), mRoot.toRef());
        addGlobalConstant("thing", mThing);
        // container
        mContainer.setName("The Generic Container");
        MOOObjectAPI.chparent(mContainer.toRef(), mThing.toRef());
        addGlobalConstant("container", mContainer);
        // player
        mPlayer.setName("The Generic Player");
        MOOObjectAPI.chparent(mPlayer.toRef(), mRoot.toRef());
        addGlobalConstant("player", mPlayer);
        // programmer
        mProgrammer.setName("The Generic Programmer");
        MOOObjectAPI.chparent(mProgrammer.toRef(), mPlayer.toRef());
        addGlobalConstant("prog", mProgrammer);
        // wizard
        mWizard.setName("The Generic Wizard");
        MOOObjectAPI.chparent(mWizard.toRef(), mProgrammer.toRef());
        addGlobalConstant("wiz", mWizard);
        // god
        mGod.setName("Goddess");
        MOOObjectAPI.chparent(mGod.toRef(), mWizard.toRef());
        // entry
        mEntryRoom.setName("Entrance");
        addGlobalConstant("first_room", mWizard);
    }

    private static final String[] DO_LOGIN_COMMAND = {
            "function doLoginCommand() {",
                "if (args.length == 0) {",
                    "notify(_player, $welcome_message);",
                    "return $nothing;",
                "}",
                "if (args[0] == 'connect') {",
                    "var playerName = args[1];",
                    "var allPlayers = players();",
                    "for (var i = 0; i < allPlayers.length; i++) {",
                        "var player = toobj(allPlayers[i]);",
                        "if (player.name == playerName) {",
                            "notify(_player, '<s>Welcome back '+player.name+'.</s>');",
                            "return player;",
                        "}",
                    "}",
                    "notify(_player, '<s>I don\\'t know the name \"'+playerName+'\".</s>');",
                    "return $nothing;",
                "}",
                "if (args[0] == 'create') {",
                    "var playerName = args[1];",
                    "print('Creating '+playerName);",
                    "var allPlayers = players();",
                    "for (var i = 0; i < allPlayers.length; i++) {",
                        "var player = toobj(allPlayers[i]);",
                        "if (player.name == playerName) {",
                            "notify(_player, '<s>The name \"'+player.name+'\" is already taken.</s>');",
                            "return $nothing;",
                        "}",
                    "}",
                    "var newPlayer = create($player);",
                    "notify(_player, '<s>Welcome to the game '+playerName+'.</s>');",
                    "move(newPlayer, $first_room);",
                    "return newPlayer;",
                "}",
                "notify(_player, '<s>I don\\'t know that command.</s>');",
                "return $nothing;",
            "}",
            "doLoginCommand();"};
    
    private void defineSystem() throws MOOException
    {
        addGlobalConstant("dump_interval", new MOONumber(600));
        MOOList welcome = new MOOList();
        welcome.add("<s>Welcome to Six Mages!</s>");
        addGlobalConstant("welcome_message", welcome);
        addGlobalConstant("player_start", mEntryRoom);
        addGlobalConstant("player_class", mPlayer);
        addGlobalConstant("nothing", new MOOObjRef(-1));
        setVerbCode(mSystem, "do_login_command", DO_LOGIN_COMMAND);
    }

    private void defineRoot()
    {
    }


    private static final String[] ROOM_ACCEPT = {
            "function doAccept() {",
                "return true;",
            "}",
            "doAccept();"};

    private void defineRoom() throws MOOException
    {
        setVerbCode(mRoom, "accept", ROOM_ACCEPT);
    }

    private void defineThing()
    {
    }

    private void defineContainer()
    {
    }

    private void definePlayer()
    {
    }

    private void defineProgrammer()
    {
    }

    private void defineWizard()
    {
    }

    private void defineGod()
    {
        mGod.setPlayer(true);
    }
    
    public void run()
    {
        try
        {
            setup();
            createBaseObjectHierarchy();
            defineSystem();
            defineRoot();
            defineRoom();
            defineThing();
            defineContainer();
            definePlayer();
            defineProgrammer();
            defineWizard();
            defineGod();
            setdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void setVerbCode(MOOObject obj, String name, String... code) throws MOOException
    {
        if (!obj.hasVerb(name))
        {
            MOOList info = new MOOList();
            info.add(obj.getOwner());
            info.add("rx");
            info.add(name);
            MOOList args = new MOOList();
            args.add("none");
            args.add("none");
            args.add("none");
            MOOVerbAPI.add_verb(obj.toRef(), info, args);
        }
        MOOVerbAPI.set_verb_code(obj.toRef(), new MOOString(name), makeList(code));
    }
    
    private static MOOList makeList(String... str)
    {
        MOOList list = new MOOList();
        for (String line : str)
            list.add(line);
        return list;
    }
    
    public static void main(String[] argv)
    {
        GenerateVoice app = new GenerateVoice();
        app.run();
    }
}
