package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOMoveAPI;
import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class FuncObjectAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("create", (RetArg2)FuncObjectAPI::create);
        engine.put("chparent", (VoidArg2)FuncObjectAPI::chparent);
        engine.put("valid", (RetArg1)FuncObjectAPI::valid);
        engine.put("parent", (RetArg1)FuncObjectAPI::parent);
        engine.put("children", (RetArg1)FuncObjectAPI::children);
        engine.put("recycle", (VoidArg1)FuncObjectAPI::recycle);
        engine.put("max_object", (RetArg0)FuncObjectAPI::max_object);
        engine.put("maxObject", (RetArg0)FuncObjectAPI::max_object);
        engine.put("move", (VoidArg2)FuncObjectAPI::move);
    }
    
    public static Object create(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef parent = CoerceLogic.toObjRef(arg0);
        MOOObjRef owner = CoerceLogic.toObjRef(arg1);
        MOOObjRef ret = MOOObjectAPI.create(parent, owner);
        return CoerceLogic.toJavascript(ret);
    }

    public static void chparent(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOObjRef newParent = CoerceLogic.toObjRef(arg1);
        MOOObjectAPI.chparent(object, newParent);
    }
    
    public static Object valid(Object arg0) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOONumber ret = MOOObjectAPI.valid(object);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static Object parent(Object arg0) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOObjRef ret = MOOObjectAPI.parent(object);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static Object children(Object arg0) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOList ret = MOOObjectAPI.children(object);
        return CoerceLogic.toJavascript(ret);
    }

    public static void recycle(Object arg0) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOObjectAPI.recycle(object);
    }
    
    public static Object max_object() throws MOOException
    {
        MOONumber ret = MOOObjectAPI.max_object();
        return CoerceLogic.toJavascript(ret);
    }
    
    public static void move(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef what = CoerceLogic.toObjRef(arg0);
        MOOObjRef where = CoerceLogic.toObjRef(arg1);
        MOOMoveAPI.move(what, where);
    }
}
