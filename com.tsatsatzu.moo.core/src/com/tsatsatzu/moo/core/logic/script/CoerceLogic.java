package com.tsatsatzu.moo.core.logic.script;

import java.util.Collection;

import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;

import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class CoerceLogic
{
    public static MOOValue toValue(Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof MOOValue)
            return (MOOValue)obj;
        if (obj instanceof String)
            return new MOOString((String)obj);
        if (obj instanceof Number)
            return new MOONumber((Number)obj);
        if (obj instanceof Collection<?>)
        {
            MOOList ret = new MOOList();
            for (Object o : ((Collection<?>)obj))
                ret.add(toValue(o));
            return ret;
        }
        if (obj instanceof Object[])
        {
            MOOList ret = new MOOList();
            for (Object o : ((Object[])obj))
                ret.add(toValue(o));
            return ret;
        }
        if (obj instanceof JSObjRef)
            return ((JSObjRef)obj).getObjRef();
        if (obj instanceof Boolean)
            if ((Boolean)obj)
                return MOONumber.TRUE;
            else
                return MOONumber.FALSE;
        if (obj instanceof ScriptObjectMirror)
        {
            ScriptObjectMirror som = (ScriptObjectMirror)obj;
            if (som.isArray())
            {
                MOOList list  = new MOOList();
                for (int i = 0; som.hasSlot(i); i++)
                {
                    Object v = som.getSlot(i);
                    list.add(toValue(v));
                }
                return list;
            }
        }
        if (obj instanceof MOOObject)
            return new MOOObjRef((MOOObject)obj);
        throw new IllegalArgumentException("Cannot coerce object type "+obj.getClass().getName()+" to value");
    }
    public static MOOObjRef toObjRef(Object obj)
    {
        MOOValue val = toValue(obj);
        if (val instanceof MOOObjRef)
            return (MOOObjRef)val;
        if (val instanceof MOOString)
        {
            String str = ((MOOString)val).getValue();
            if (str.startsWith("#"))
                return new MOOObjRef(IntegerUtils.parseInt(str.substring(1)));
        }
        throw new IllegalArgumentException("Cannot coerce object type "+obj.getClass().getName()+" to ObjRef");
    }
    public static MOONumber toNumber(Object obj)
    {
        MOOValue val = toValue(obj);
        if (val instanceof MOONumber)
            return (MOONumber)val;
        throw new IllegalArgumentException("Cannot coerce object type "+obj.getClass().getName()+" to Number");
    }
    public static MOOString toString(Object obj)
    {
        MOOValue val = toValue(obj);
        if (val instanceof MOOString)
            return (MOOString)val;
        throw new IllegalArgumentException("Cannot coerce object type "+obj.getClass().getName()+" to String");
    }
    public static MOOList toList(Object obj)
    {
        MOOValue val = toValue(obj);
        if (val instanceof MOOList)
            return (MOOList)val;
        throw new IllegalArgumentException("Cannot coerce object type "+obj.getClass().getName()+" to List");
    }
    public static Object toJavascript(MOOValue val)
    {
        if (val == null)
            return null;
        if (val instanceof MOOList)
        {
            MOOList list = (MOOList)val;
            Object[] ret = new Object[list.size()];
            for (int i = 0; i < list.size(); i++)
                ret[i] = toJavascript(list.get(i));
            return ret;
        }
        else if (val instanceof MOONumber)
            return ((MOONumber)val).getValue();
        else if (val instanceof MOOString)
            return ((MOOString)val).getValue();
        else if (val instanceof MOOObjRef)
            return new JSObjRef((MOOObjRef)val);
        throw new IllegalArgumentException("Cannot coerce value "+val.getClass().getName()+" to object");
    }
    public static Object toJavascriptBoolean(MOOValue val)
    {
        if (val == null)
            return false;
        if (val instanceof MOOList)
        {
            MOOList list = (MOOList)val;
            return list.getValue().size() > 0;
        }
        else if (val instanceof MOONumber)
            return ((MOONumber)val).getValue().intValue() != 0;
        else if (val instanceof MOOString)
            return !StringUtils.isTrivial(((MOOString)val).getValue());
        else if (val instanceof MOOObjRef)
            return ((MOOObjRef)val).getValue() != 0;
        throw new IllegalArgumentException("Cannot coerce value "+val.getClass().getName()+" to object");
    }
}
