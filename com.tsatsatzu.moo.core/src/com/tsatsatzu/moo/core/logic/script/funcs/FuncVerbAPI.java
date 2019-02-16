package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class FuncVerbAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("verbs", (RetArg1)FuncVerbAPI::verbs);
        engine.put("verb_info", (RetArg2)FuncVerbAPI::verb_info);
        engine.put("verbInfo", (RetArg2)FuncVerbAPI::verb_info);
        engine.put("set_verb_info", (VoidArg3)FuncVerbAPI::set_verb_info);
        engine.put("setVerbInfo", (VoidArg3)FuncVerbAPI::set_verb_info);
        engine.put("verb_args", (RetArg2)FuncVerbAPI::verb_args);
        engine.put("verbArgs", (RetArg2)FuncVerbAPI::verb_args);
        engine.put("set_verb_args", (VoidArg3)FuncVerbAPI::set_verb_args);
        engine.put("setVerbArgs", (VoidArg3)FuncVerbAPI::set_verb_args);
        engine.put("add_verb", (VoidArg3)FuncVerbAPI::add_verb);
        engine.put("addVerb", (VoidArg3)FuncVerbAPI::add_verb);
        engine.put("delete_verb", (VoidArg2)FuncVerbAPI::delete_verb);
        engine.put("deleteVerb", (VoidArg2)FuncVerbAPI::delete_verb);
        engine.put("verb_code", (RetArg4)FuncVerbAPI::verb_code);
        engine.put("verbCode", (RetArg4)FuncVerbAPI::verb_code);
        engine.put("set_verb_code", (RetArg3)FuncVerbAPI::set_verb_code);
        engine.put("setVerbCode", (RetArg3)FuncVerbAPI::set_verb_code);
    }
    
    public static Object verbs(Object arg0) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOList verbs = MOOVerbAPI.verbs(object);
        return CoerceLogic.toJavascript(verbs);
    }
    
    public static Object verb_info(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOList ret = MOOVerbAPI.verb_info(object, name);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static void set_verb_info(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOList info = CoerceLogic.toList(arg2);
        MOOVerbAPI.verb_set_info(object, name, info);
    }
    
    public static Object verb_args(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOList ret = MOOVerbAPI.verb_args(object, name);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static void set_verb_args(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOList info = CoerceLogic.toList(arg2);
        MOOVerbAPI.verb_set_args(object, name, info);
    }
    
    public static void add_verb(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOList info = CoerceLogic.toList(arg1);
        MOOList args = CoerceLogic.toList(arg2);
        MOOVerbAPI.add_verb(object, info, args);
    }
    
    public static void delete_verb(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOVerbAPI.delete_verb(object, name);
    }
    
    public static Object verb_code(Object arg0, Object arg1, Object arg2, Object arg3) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOONumber fullyParen = CoerceLogic.toNumber(arg2);
        MOONumber indent = CoerceLogic.toNumber(arg3);
        MOOList ret = MOOVerbAPI.verb_code(object, name, fullyParen, indent);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static Object set_verb_code(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOList code = CoerceLogic.toList(arg2);
        MOOList ret = MOOVerbAPI.set_verb_code(object, name, code);
        return CoerceLogic.toJavascript(ret);
    }
}
