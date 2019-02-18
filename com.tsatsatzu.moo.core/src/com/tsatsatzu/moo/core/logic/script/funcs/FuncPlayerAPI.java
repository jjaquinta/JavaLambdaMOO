package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOPlayerAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncPlayerAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject players = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOList ret = MOOPlayerAPI.players();
                return ret;
            }
        }; 
        JSObject set_player_flag = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef playerRef = CoerceLogic.toObjRef(args[0]);
                MOONumber value = CoerceLogic.toNumber(args[1]);
                MOOPlayerAPI.set_player_flag(playerRef, value);
                return null;
            }
        }; 
        JSObject is_player = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef playerRef = CoerceLogic.toObjRef(args[0]);
                MOONumber ret = MOOPlayerAPI.is_player(playerRef);
                return ret;
            }
        }; 
        engine.put("players", players);
        engine.put("set_player_flag", set_player_flag);
        engine.put("setPlayerFlag", set_player_flag);
        engine.put("is_player", is_player);
        engine.put("isPlayer", is_player);
    }
}
