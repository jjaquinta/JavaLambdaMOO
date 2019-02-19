package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOConvAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
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
        engine.put("toobj", toobj);
        engine.put("to_obj", toobj);
        engine.put("toObj", toobj);
    }
}
