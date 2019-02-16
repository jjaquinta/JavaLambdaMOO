package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOPlayerAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class FuncPlayerAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("players", (RetArg0)FuncPlayerAPI::players);
        engine.put("set_player_flag", (VoidArg2)FuncPlayerAPI::set_player_flag);
        engine.put("setPlayerFlag", (VoidArg2)FuncPlayerAPI::set_player_flag);
        engine.put("is_player", (RetArg1)FuncPlayerAPI::is_player);
        engine.put("isPlayer", (RetArg1)FuncPlayerAPI::is_player);
    }
    
    public static Object players() throws MOOException
    {
        MOOList ret = MOOPlayerAPI.players();
        return CoerceLogic.toJavascript(ret);
    }

    public static Object is_player(Object arg0) throws MOOException
    {
        MOOObjRef playerRef = CoerceLogic.toObjRef(arg0);
        MOONumber ret = MOOPlayerAPI.is_player(playerRef);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static void set_player_flag(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef playerRef = CoerceLogic.toObjRef(arg0);
        MOONumber value = CoerceLogic.toNumber(arg1);
        MOOPlayerAPI.set_player_flag(playerRef, value);
    }
}
