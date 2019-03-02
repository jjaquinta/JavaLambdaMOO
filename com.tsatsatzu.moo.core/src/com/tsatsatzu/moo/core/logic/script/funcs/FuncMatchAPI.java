package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOMatchAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOMap;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncMatchAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject match_word = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString word = CoerceLogic.toString(args[0]);
                MOOString pattern = CoerceLogic.toString(args[1]);
                MOOMap match = MOOMatchAPI.match_word(word, pattern);
                return match;
            }
        };
        JSObject match_object = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString word = CoerceLogic.toString(args[0]);
                MOOObjRef obj = CoerceLogic.toObjRef(args[1]);
                MOOMap match = MOOMatchAPI.match_object(word, obj);
                return match;
            }
        };
        JSObject match_list = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString word = CoerceLogic.toString(args[0]);
                MOOList list = CoerceLogic.toList(args[1]);
                MOONumber limit = (args.length < 2) ? new MOONumber(1) : CoerceLogic.toNumber(args[2]);
                MOOList object = MOOMatchAPI.match_list(word, list, limit);
                return object;
            }
        };
        JSObject match_verb = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString word = CoerceLogic.toString(args[0]);
                MOOObjRef pattern = CoerceLogic.toObjRef(args[1]);
                MOOMap object = MOOMatchAPI.match_verb(word, pattern);
                return object;
            }
        };
        JSObject match_verbs = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString word = CoerceLogic.toString(args[0]);
                MOOList list = CoerceLogic.toList(args[1]);
                MOONumber limit = (args.length < 2) ? new MOONumber(1) : CoerceLogic.toNumber(args[2]);
                MOOList object = MOOMatchAPI.match_verbs(word, list, limit);
                return object;
            }
        };
        engine.put("match_word", match_word);
        engine.put("matchWord", match_word);
        engine.put("match_object", match_object);
        engine.put("matchObject", match_object);
        engine.put("match_list", match_list);
        engine.put("matchList", match_list);
        engine.put("match_verb", match_verb);
        engine.put("matchVerb", match_verb);
        engine.put("match_verbs", match_verbs);
        engine.put("matchVerbs", match_verbs);
    }
}
