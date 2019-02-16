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

public class FuncPropAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("properties", (RetArg1)FuncPropAPI::properties);
        engine.put("property_info", (RetArg2)FuncPropAPI::property_info);
        engine.put("propertyInfo", (RetArg2)FuncPropAPI::property_info);
        engine.put("set_property_info", (VoidArg3)FuncPropAPI::set_property_info);
        engine.put("setPropertyInfo", (VoidArg3)FuncPropAPI::set_property_info);
        engine.put("add_property", (VoidArg4)FuncPropAPI::add_property);
        engine.put("addProperty", (VoidArg4)FuncPropAPI::add_property);
        engine.put("delete_property", (VoidArg2)FuncPropAPI::delete_property);
        engine.put("deleteProperty", (VoidArg2)FuncPropAPI::delete_property);
        engine.put("is_clear_property", (RetArg2)FuncPropAPI::is_clear_property);
        engine.put("isClearProperty", (RetArg2)FuncPropAPI::is_clear_property);
        engine.put("clear_property", (VoidArg2)FuncPropAPI::clear_property);
        engine.put("clearProperty", (VoidArg2)FuncPropAPI::clear_property);
        engine.put("get_property", (RetArg2)FuncPropAPI::get_property);
        engine.put("getProperty", (RetArg2)FuncPropAPI::get_property);
        engine.put("set_property", (VoidArg3)FuncPropAPI::set_property);
        engine.put("setProperty", (VoidArg3)FuncPropAPI::set_property);
    }
    
    public static Object properties(Object arg0) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOList Propertys = MOOPropertyAPI.properties(object);
        return CoerceLogic.toJavascript(Propertys);
    }
    
    public static Object property_info(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOList ret = MOOPropertyAPI.property_info(object, name);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static void set_property_info(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOList info = CoerceLogic.toList(arg2);
        MOOPropertyAPI.set_property_info(object, name, info);
    }
    
    public static void add_property(Object arg0, Object arg1, Object arg2, Object arg3) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOValue value = CoerceLogic.toValue(arg2);
        MOOList info = CoerceLogic.toList(arg3);
        MOOPropertyAPI.add_property(object, name, value, info);
    }
    
    public static void delete_property(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOPropertyAPI.delete_property(object, name);
    }
    
    public static Object is_clear_property(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOONumber ret = MOOPropertyAPI.is_clear_property(object, name);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static void clear_property(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOPropertyAPI.clear_property(object, name);
    }
    
    public static Object get_property(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOValue val = MOOPropertyAPI.get_property(object, name);
        return CoerceLogic.toJavascript(val);
    }
    
    public static void set_property(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOValue val = CoerceLogic.toValue(arg2);
        MOOPropertyAPI.set_property(object, name, val);
    }
}
