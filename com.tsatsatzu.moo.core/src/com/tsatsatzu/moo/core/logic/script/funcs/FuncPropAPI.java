package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOPropertyAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncPropAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject properties = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOList Propertys = MOOPropertyAPI.properties(object);
                return Propertys;
            }
        }; 
        JSObject property_info = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOList ret = MOOPropertyAPI.property_info(object, name);
                return ret;
            }
        }; 
        JSObject set_property_info = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOList info = CoerceLogic.toList(args[2]);
                MOOPropertyAPI.set_property_info(object, name, info);
                return null;
            }
        }; 
        JSObject add_property = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOValue value = CoerceLogic.toValue(args[2]);
                MOOList info = CoerceLogic.toList(args[3]);
                MOOPropertyAPI.add_property(object, name, value, info);
                return null;
            }
        }; 
        JSObject delete_property = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOPropertyAPI.delete_property(object, name);
                return null;
            }
        }; 
        JSObject is_clear_property = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOONumber ret = MOOPropertyAPI.is_clear_property(object, name);
                return ret;
            }
        }; 
        JSObject clear_property = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOPropertyAPI.clear_property(object, name);
                return null;
            }
        }; 
        JSObject get_property = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOValue val = MOOPropertyAPI.get_property(object, name);
                return val;
            }
        }; 
        JSObject set_property = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOValue val = CoerceLogic.toValue(args[2]);
                MOOPropertyAPI.set_property(object, name, val);
                return null;
            }
        }; 
        engine.put("properties", properties);
        engine.put("property_info", property_info);
        engine.put("propertyInfo", property_info);
        engine.put("set_property_info", set_property_info);
        engine.put("setPropertyInfo", set_property_info);
        engine.put("add_property", add_property);
        engine.put("addProperty", add_property);
        engine.put("delete_property", delete_property);
        engine.put("deleteProperty", delete_property);
        engine.put("is_clear_property", is_clear_property);
        engine.put("isClearProperty", is_clear_property);
        engine.put("clear_property", clear_property);
        engine.put("clearProperty", clear_property);
        engine.put("get_property", get_property);
        engine.put("getProperty", get_property);
        engine.put("set_property", set_property);
        engine.put("setProperty", set_property);
    }
}
