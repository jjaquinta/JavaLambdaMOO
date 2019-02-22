package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOMoveAPI;
import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncObjectAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject create = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef parent = CoerceLogic.toObjRef(args[0]);
                MOOObjRef owner = (args.length > 1) ? CoerceLogic.toObjRef(args[1]) : MOOProgrammerLogic.getProgrammerRef();
                MOOObjRef ret = MOOObjectAPI.create(parent, owner);
                return ret;
            }
        }; 
        JSObject chparent = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOObjRef newParent = CoerceLogic.toObjRef(args[1]);
                MOOObjectAPI.chparent(object, newParent);
                return null;
            }
        }; 
        JSObject valid = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOONumber ret = MOOObjectAPI.valid(object);
                return ret;
            }
        }; 
        JSObject parent = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOObjRef ret = MOOObjectAPI.parent(object);
                return ret;
            }
        }; 
        JSObject children = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOList ret = MOOObjectAPI.children(object);
                return ret;
            }
        }; 
        JSObject recycle = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOObjectAPI.recycle(object);
                return null;
            }
        }; 
        JSObject max_object = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber ret = MOOObjectAPI.max_object();
                return ret;
            }
        }; 
        JSObject move = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef what = CoerceLogic.toObjRef(args[0]);
                MOOObjRef where = CoerceLogic.toObjRef(args[1]);
                MOOMoveAPI.move(what, where);
                return null;
            }
        }; 
        engine.put("create", create);
        engine.put("chparent", chparent);
        engine.put("valid", valid);
        engine.put("parent", parent);
        engine.put("children", children);
        engine.put("recycle", recycle);
        engine.put("max_object", max_object);
        engine.put("maxObject", max_object);
        engine.put("move", move);
    }
}
