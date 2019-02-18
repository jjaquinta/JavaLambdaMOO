package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncVerbAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject verbs = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOList ret = MOOVerbAPI.verbs(object);
                return ret;
            }
        }; 
        JSObject verb_info = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOOList ret = MOOVerbAPI.verb_info(object, name);
                return ret;
            }
        }; 
        JSObject set_verb_info = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOOList info = CoerceLogic.toList(args[2]);
                MOOVerbAPI.verb_set_info(object, name, info);
                return null;
            }
        }; 
        JSObject verb_args = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOOList ret = MOOVerbAPI.verb_args(object, name);
                return ret;
            }
        }; 
        JSObject set_verb_args = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOOList info = CoerceLogic.toList(args[2]);
                MOOVerbAPI.verb_set_args(object, name, info);
                return null;
            }
        }; 
        JSObject add_verb = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOList info = CoerceLogic.toList(args[1]);
                MOOList vargs = CoerceLogic.toList(args[2]);
                MOOVerbAPI.add_verb(object, info, vargs);
                return null;
            }
        }; 
        JSObject delete_verb = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOOVerbAPI.delete_verb(object, name);
                return null;
            }
        }; 
        JSObject verb_code = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOONumber fullyParen = CoerceLogic.toNumber(args[2]);
                MOONumber indent = CoerceLogic.toNumber(args[3]);
                MOOList ret = MOOVerbAPI.verb_code(object, name, fullyParen, indent);
                return ret;
            }
        }; 
        JSObject set_verb_code = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOValue name = CoerceLogic.toValue(args[1]);
                MOOList code = CoerceLogic.toList(args[2]);
                MOOList ret = MOOVerbAPI.set_verb_code(object, name, code);
                return ret;
            }
        }; 
        engine.put("verbs", verbs);
        engine.put("verb_info", verb_info);
        engine.put("verbInfo", verb_info);
        engine.put("set_verb_info", set_verb_info);
        engine.put("setVerbInfo", set_verb_info);
        engine.put("verb_args", verb_args);
        engine.put("verbArgs", verb_args);
        engine.put("set_verb_args", set_verb_args);
        engine.put("setVerbArgs", set_verb_args);
        engine.put("add_verb", add_verb);
        engine.put("addVerb", add_verb);
        engine.put("delete_verb", delete_verb);
        engine.put("deleteVerb", delete_verb);
        engine.put("verb_code", verb_code);
        engine.put("verbCode", verb_code);
        engine.put("set_verb_code", set_verb_code);
        engine.put("setVerbCode", set_verb_code);
    }
}
