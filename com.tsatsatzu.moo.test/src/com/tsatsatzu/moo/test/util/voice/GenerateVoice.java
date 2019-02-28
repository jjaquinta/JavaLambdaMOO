package com.tsatsatzu.moo.test.util.voice;

import java.io.File;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.api.MOOPropertyAPI;
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
    private MOOObject mExitRoom;
    
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
        mExitRoom = MOODbLogic.newInstance(mRoom.getOID());
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
        addGlobalConstant("first_room", mEntryRoom);
        // exit
        mExitRoom.setName("Exit");
        addGlobalConstant("limbo", mExitRoom);
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
                            "notify(_player, '<s>Welcome back '+player.title()+'.</s>');",
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
                            "notify(_player, '<s>The name \"'+playerName+'\" is already taken.</s>');",
                            "return $nothing;",
                        "}",
                    "}",
                    "var newPlayer = create($player);",
                    "set_player_flag(newPlayer, true);",
                    "newPlayer.owner = newPlayer;",
                    "notify(_player, '<s>Welcome to the game '+playerName+'.</s>');",
                    "_player = newPlayer;",
                    "print('Setting new player '+_player);",
                    "move(_player, $first_room);",
                    "print('Moved '+playerName+' to first room');",
                    "return newPlayer;",
                "}",
                "notify(_player, '<s>I don\\'t know that command.</s>');",
                "return $nothing;",
            "}",
            "doLoginCommand();"};
    private static final String[] USER_CLIENT_DISCONNECTED = {
            "function userDisconnected() {",
                "if (callers().length > 1) {",
                    "return;",
                 "}",
                 "if (toint(args[0]) < 0) {",
                     "//not logged in user.  probably should do something clever here involving Carrot's no-spam hack.  --yduJ",
                     "return;",
                 "}",
                 "var user = args[0];",
                 "user.last_disconnect_time = time();",
                 "set_task_perms(user);",
                 "move(user, $limbo);",
            "}",
            "userDisconnected();"
    };
    private static final String[] USER_CREATED = {
        "function userCreated() {",
            "print('UserCreated');",
            "if (callers().length > 1) {",
                "print('bad callers');",
                "return;",
             "}",
             "if (toint(args[0]) < 0) {",
                 "print('not actually logged in');",
                 "//not logged in user.  probably should do something clever here involving Carrot's no-spam hack.  --yduJ",
                 "return;",
             "}",
             "var user = args[0];",
             "set_task_perms(user);",
             "print('Moving to room');",
             "move(user, $first_room);",
             "$first_room.announce('<s>'+_player.title()+' has connected.</a>');",
        "}",
        "userCreated();"
    };
    
    private void defineSystem() throws MOOException
    {
        mSystem.setOwner(mGod.toRef());
        addGlobalConstant("dump_interval", new MOONumber(600));
        MOOList welcome = new MOOList();
        welcome.add("<s>Welcome to Six Mages!</s>");
        addGlobalConstant("welcome_message", welcome);
        addGlobalConstant("player_start", mEntryRoom);
        addGlobalConstant("player_class", mPlayer);
        addGlobalConstant("nothing", new MOOObjRef(-1));
        setVerbCode(mSystem, "do_login_command", DO_LOGIN_COMMAND);
        setVerbCode(mSystem, "user_disconnected user_client_disconnected", USER_CLIENT_DISCONNECTED);
        setVerbCode(mSystem, "user_created user_connected", USER_CREATED);
    }

    private static final String[] ROOT_ACCEPT = {
            "function doAccept() {",
                "set_task_perms(caller_perms());",
                "return _this.acceptable(args[0]);",
            "}",
            "doAccept();"};
    private static final String[] ROOT_ACCEPTABLE = {
            "function doAcceptable() {",
                "//intended as a 'quiet' way to determine if :accept will succeed. Currently, some objects have a noisy :accept verb since it is the only thing that a builtin move() call is guaranteed to call.",
                "//if you want to tell, before trying, whether :accept will fail, use :acceptable instead. Normally, they'll do the same thing.",
                "return false;",
            "}",
            "doAcceptable();"};    
    private static final String[] ROOT_ANNOUNCE = {
            "function doAnnounce() {",
            "}",
            "doAnnounce();"};    
    private static final String[] ROOT_TELL = {
            "function doTell() {",
                "print('ROOT.tell, _player='+_player);",
                "_this.notify(tostr(args));",
            "}",
            "doTell();"};    
    private static final String[] ROOT_TELL_LINES = {
            "function doTell() {",
                "var lines = args[0];",
                "if (typeof lines == 'array') {",
                    "for (var i = 0; i < lines.length; i++){",
                        "_this.tell(lines[i]);",
                    "}",
                "} else {",
                    "_this.tell(lines);",
                "}",
            "}",
            "doTell();"};    
    private static final String[] ROOT_NOTIFY = {
            "function doNotify() {",
                "print('ROOT.notify, _player='+_player);",
                "if (is_player(_this)) {",
                    "notify(_this, tostr(args));",
                "}",
            "}",
            "doNotify();"};    
    private static final String[] ROOT_LOOK_SELF = {
            "function doLookSelf() {",
                "desc = _this.getDescription();",
                "if (desc)",
                    "_player.tell_lines(desc);",
                "else",
                    "_player.tell('<s>You see nothing special.</s>');",
            "}",
            "doLookSelf();"};    
    private static final String[] ROOT_TITLE = {
            "function doTitle() {",
                "return _this.name;",
            "}",
            "doTitle();"};    
    private static final String[] ROOT_DESCRIPTION = {
            "function doDescription() {",
                "return _this.description;",
            "}",
            "doDescription();"};    
    private static final String[] ROOT_CONTENTS = {
            "function doContents() {",
                "return _this.contents;",
            "}",
            "doContents();"};    

    private void defineRoot() throws MOOException
    {
        mRoot.setOwner(mGod.toRef());
        addProperty(mRoot, "aliases", new MOOList(), "rc");
        addProperty(mRoot, "description", new MOOString(""), "rc");
        setVerbCode(mRoot, "accept", ROOT_ACCEPT);
        setVerbCode(mRoot, "acceptable", ROOT_ACCEPTABLE);
        setVerbCode(mRoot, "announce*_all_but", ROOT_ANNOUNCE);
        setVerbCode(mRoot, "tell", ROOT_TELL);
        setVerbCode(mRoot, "tell_lines", ROOT_TELL_LINES);
        setVerbCode(mRoot, "notify", ROOT_NOTIFY);
        setVerbCode(mRoot, "look_self", ROOT_LOOK_SELF);
        setVerbCode(mRoot, "title", ROOT_TITLE);
        setVerbCode(mRoot, "getDescription", ROOT_DESCRIPTION);
        setVerbCode(mRoot, "getContents", ROOT_CONTENTS);
    }

    private static final String[] ROOM_ACCEPTABLE = {
            "function doAcceptable() {",
                "return true;",
            "}",
            "doAcceptable();"};    
    private static final String[] ROOM_ANNOUNCE = {
            "function doAnnounce() {",
                "var cs = _this.getContents();",
                "for (var i = 0; i < cs.length; i++) {",
                    "var dude = toobj(cs[i]);",
                    "if (dude == _player) continue;",
                    "try {",
                        "dude.tell.apply(dude.tell, args);",
                    "} catch (err) {}",
                "}",
            "}",
            "doAnnounce();"};    
    private static final String[] ROOM_ANNOUNCE_ALL = {
            "function doAnnounceAll() {",
                "var cs = _this.getContents();",
                "for (var i = 0; i < cs.length; i++) {",
                    "var dude = cs[i];",
                    "try {",
                        "dude.tell.apply(dude.tell, args);",
                    "} catch (err) {}",
                "}",
            "}",
            "doAnnounceAll();"};    
    private static final String[] ROOM_ANNOUNCE_ALL_BUT = {
            "function doAnnounceAllBut() {",
                "var but = args[0];",
                "args.shift();",
                "var cs = _this.getContents();",
                "for (var i = 0; i < cs.length; i++) {",
                    "var dude = cs[i];",
                    "if (but.includes(dude)) continue;",
                    "try {",
                        "dude.tell.apply(dude.tell, args);",
                    "} catch (err) {}",
                "}",
            "}",
            "doAnnounceAllBut();"};    
    private static final String[] ROOM_ENTERFUNC = {
            "function doEnterFunc() {",
                "var object = args[0];",
                "if (is_player(object) && (object.location.equals(_this))) {",
                    "print('ROOM.enterfunc look_self, _player='+_player);",
                    "_player = object;",
                    "_this.look_self();",
                "}",
            "}",
            "doEnterFunc();"};    
    private static final String[] ROOM_EXITFUNC = {
            "function doExitFunc() {",
                "}",
            "doExitFunc();"};    
    private static final String[] ROOM_LOOK_SELF = {
            "function doLookSelf() {",
                "print('ROOM.look_self, _player='+_player);",
                "_player.tell(_this.getDescription());",
            "}",
            "doLookSelf();"};    
    private static final String[] ROOM_TELL_CONTENTS = {
            "function doTellContents() {",
                "var contents = args[0];",
                "ctype = args[1];",
                "if ((!_this.dark) && (contents.length > 0)) {",
                    "if (ctype == 0) {",
                        "_player.tell('<s>Contents:</s>');",
                        "for (var i = 0; i < contents.length; i++) {",
                            "var thing = contents[i];",
                            "_player.tell('<s>  ', thing.title(), '</s>');",
                        "}",
                    "} else if (ctype == 1) {",
                        "for (var i = 0; i < contents.length; i++) {",
                            "var thing = contents[i];",
                            "if (is_player(thing)) {",
                                "_player.tell('<s>', thing.title(), 'is  here.</s>');",
                            "} else {",
                                "_player.tell('<s>You see ', thing.title(), ' here.</s>');",
                            "}",
                        "}",
                    "} else if (ctype == 2) {",
                        "_player.tell('<s>You see ', $string_utils:title_list(contents), ' here.</s>');",
                    "} else if (ctype == 3) {",
                        "var players = [];",
                        "var things = {};",
                        "for (var i = 0; i < contents.length; i++) {",
                            "x = contents[i];",
                            "if (is_player(x)) {",
                                "players.append(x);",
                            "} else {",
                                "things.append(x);",
                            "}",
                        "}",
                        "if (things.length > 0) {",
                            "_player.tell('<s>You see ', $string_utils:title_list(things), ' here.</s>');",
                        "}",
                        "if (players.length > 0) {",
                            "_player.tell($string_utils:title_listc(players), (players.length == 1) ? ' ' + ' are here.', '</s>');",
                        "}",
                    "}",
                "}",
            "}",
            "doTellContents();"};    


    private void defineRoom() throws MOOException
    {
        mRoom.setOwner(mGod.toRef());
        setVerbCode(mRoom, "acceptable", ROOM_ACCEPTABLE);
        setVerbCode(mRoom, "announce", ROOM_ANNOUNCE);
        setVerbCode(mRoom, "announce_all", ROOM_ANNOUNCE_ALL);
        setVerbCode(mRoom, "announce_all_but", ROOM_ANNOUNCE_ALL_BUT);
        setVerbCode(mRoom, "enterfunc", ROOM_ENTERFUNC);
        setVerbCode(mRoom, "exitfunc", ROOM_EXITFUNC);
        setVerbCode(mRoom, "look_self", ROOM_LOOK_SELF);
        setVerbCode(mRoom, "tell_contents", ROOM_TELL_CONTENTS);
    }

    private void defineThing() throws MOOException
    {
        mThing.setOwner(mGod.toRef());
    }

    private static final String[] CONTAINER_ACCEPTABLE = {
            "function doAcceptable() {",
                "return !is_player(args[0]);",
            "}",
            "doAcceptable();"};    
    private static final String[] CONTAINER_LOOK_SELF = {
            "function doLookSelf() {",
                "pass();",
                "if (!_this.dark) {",
                    "_this.tell_contents();",
                "}",
            "}",
            "doLookSelf();"};    

    private void defineContainer() throws MOOException
    {
        mContainer.setOwner(mGod.toRef());
        addProperty(mContainer, "dark", MOONumber.FALSE, "rwc");
        setVerbCode(mContainer, "acceptable", CONTAINER_ACCEPTABLE);
        setVerbCode(mContainer, "look_self", CONTAINER_LOOK_SELF);
    }

    private static final String[] PLAYER_ACCEPTABLE = {
            "function doAcceptable() {",
                "return !is_player(args[0]);",
            "}",
            "doAcceptable();"};    
    private static final String[] PLAYER_LOOK_SELF = {
            "function doLookSelf() {",
                "_player.tell(_this.title());",
                "pass();",
                "if (!(connected_players().includes(_this)) {",
                    "_player.tell('<s>'+_this.title()+' is sleeping.</s>');",
                "} else if (idle_seconds(_this) < 60) {",
                    "_player.tell('<s>'+_this.title()+' is awake and looks alert.</s>');",
                "} else {",
                    "_player.tell('<s>'+_this.title()+'is awake, but has been staring off into space for '+idle_seconds(_this)+' seconds.</s>');",
                "}",
                "if (_this.getContents()) {",
                    "_this.tell_contents(_this.getContents());",
                "}",
            "}",
            "doLookSelf();"};    

    private void definePlayer() throws MOOException
    {
        mPlayer.setOwner(mGod.toRef());
        addProperty(mPlayer, "last_disconnect_time", new MOONumber(0), "rwc");
        setVerbCode(mPlayer, "acceptable", PLAYER_ACCEPTABLE);
        setVerbCode(mPlayer, "look_self", PLAYER_LOOK_SELF);
    }

    private void defineProgrammer() throws MOOException
    {
        mProgrammer.setOwner(mGod.toRef());
    }

    private void defineWizard() throws MOOException
    {
        mWizard.setOwner(mGod.toRef());
    }

    private void defineGod() throws MOOException
    {
        mGod.setOwner(mGod.toRef());
        mGod.setPlayer(true);
    }

    private void defineEntryRoom() throws MOOException
    {
        mEntryRoom.setOwner(mGod.toRef());
        MOOPropertyAPI.set_property(mEntryRoom.toRef(), new MOOString("description"), new MOOString("<s>This is an elegant foyeur of white marble.</s>"));
    }

    private void defineExitRoom() throws MOOException
    {
        mExitRoom.setOwner(mGod.toRef());
        MOOPropertyAPI.set_property(mExitRoom.toRef(), new MOOString("description"), new MOOString("<s>This is a dark sub-basement made of hard packed earth.</s>"));
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
            defineEntryRoom();
            defineExitRoom();
            setdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void addProperty(MOOObject obj, String name, MOOValue value, String perms) throws MOOException
    {
        MOOList info = new MOOList();
        info.add(obj.getOwner());
        info.add(perms);
        MOOPropertyAPI.add_property(obj.toRef(), new MOOString(name), value, info);
    }
    
    private static boolean hasDefinedVerb(MOOObject obj, String name)
    {
        for (MOOVerb verb : obj.getVerbs())
            if (verb.getName().equals(name))
                return true;
        return false;
    }

    private static void setVerbCode(MOOObject obj, String name, String... code) throws MOOException
    {
        if (!hasDefinedVerb(obj, name))
        {
            MOOList info = new MOOList();
            info.add(obj.getOwner());
            info.add("rx");
            info.add(name);
            MOOList args = new MOOList();
            args.add("this");
            args.add("none");
            args.add("this");
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
