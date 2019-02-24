package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOConvAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncConvAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject toobj = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOValue val = CoerceLogic.toValue(args[0]);
                MOOObjRef object = MOOConvAPI.toobj(val);
                return object;
            }
        }; 
        JSObject tostr = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOValue val = CoerceLogic.toValue(args[0]);
                MOOString object = MOOConvAPI.tostr(val);
                return object;
            }
        }; 
        JSObject toint = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOValue val = CoerceLogic.toValue(args[0]);
                MOONumber object = MOOConvAPI.toint(val);
                return object;
            }
        }; 
        JSObject typeof = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOValue val = CoerceLogic.toValue(args[0]);
                MOONumber object = MOOConvAPI.typeof(val);
                return object;
            }
        }; 
        JSObject time = new JSFuncObject() {
            @Override
            public MOONumber call(Object... args) throws MOOException
            {
                MOONumber object = MOOConvAPI.time();
                return object;
            }
        }; 
        JSObject ctime = new JSFuncObject() {
            @Override
            public MOOString call(Object... args) throws MOOException
            {
                MOONumber val = (args.length > 0) ? CoerceLogic.toNumber(args[0]) : null;
                MOOString object = MOOConvAPI.ctime(val);
                return object;
            }
        }; 
        engine.put("toobj", toobj);
        engine.put("to_obj", toobj);
        engine.put("toObj", toobj);
        engine.put("tostr", tostr);
        engine.put("to_str", tostr);
        engine.put("toStr", tostr);
        engine.put("toint", toint);
        engine.put("to_int", toint);
        engine.put("toInt", toint);
        engine.put("typeof", typeof);
        engine.put("time", time);
        engine.put("ctime", ctime);
    }
}
