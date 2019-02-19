package com.tsatsatzu.moo.core.logic.script.funcs;

import com.tsatsatzu.moo.core.api.MOOConvAPI;
import com.tsatsatzu.moo.core.api.MOOVerbAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class PythonAPI
{
    public static Object toobj(Object arg0) throws MOOException
    {
        MOOValue val = CoerceLogic.toValue(arg0);
        MOOObjRef object = MOOConvAPI.toobj(val);
        return CoerceLogic.toJavascript(object);
    }
    
    public static void set_verb_info(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOValue name = CoerceLogic.toValue(arg1);
        MOOList info = CoerceLogic.toList(arg2);
        MOOVerbAPI.verb_set_info(object, name, info);
    }

}
