package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOConvAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class FuncConvAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("toobj", (RetArg1)FuncConvAPI::toobj);
        engine.put("to_obj", (RetArg1)FuncConvAPI::toobj);
        engine.put("toObj", (RetArg1)FuncConvAPI::toobj);
    }
    
    public static Object toobj(Object arg0) throws MOOException
    {
        MOOValue val = CoerceLogic.toValue(arg0);
        MOOObjRef object = MOOConvAPI.toobj(val);
        return CoerceLogic.toJavascript(object);
    }
}
