package com.tsatsatzu.moo.core.data;

import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOMap;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;

import jo.audio.util.IJSONAble;
import jo.audio.util.JSONUtils;
import jo.util.utils.obj.BooleanUtils;

public abstract class MOOValue implements IJSONAble
{
    // utilities
    public static JSONObject toJSON(MOOValue val)
    {
        JSONObject json = val.toJSON();
        return json;
    }

    public static MOOValue newFromJSON(JSONObject json)
    {
        MOOValue v;
        String type = JSONUtils.getString(json, "type");
        if (type.equals("list"))
            v = new MOOList();
        else if (type.equals("map"))
            v = new MOOMap();
        else if (type.equals("num"))
            v = new MOONumber();
        else if (type.equals("obj"))
            v = new MOOObjRef();
        else if (type.equals("str"))
            v = new MOOString();
        else
            throw new IllegalArgumentException("Unrecognized MOOValue "+json.toJSONString());
        v.fromJSON(json);
        return v;
    }
    
    @Override
    public abstract JSONObject toJSON();
    @Override
    public abstract void fromJSON(JSONObject o);

    public boolean toBoolean()
    {
        if (this instanceof MOONumber)
            return BooleanUtils.parseBoolean(((MOONumber)this).getValue());
        throw new IllegalStateException("Cannot convert "+getClass().getSimpleName()+"="+this+" to boolean");
    }
    public MOOObjRef toObjRef()
    {
        if (this instanceof MOOObjRef)
            return (MOOObjRef)this;
        throw new IllegalStateException("Cannot convert "+getClass().getSimpleName()+"="+this+" to object reference");
    }
    public MOOString toStringVal()
    {
        if (this instanceof MOOString)
            return (MOOString)this;
        throw new IllegalStateException("Cannot convert "+getClass().getSimpleName()+"="+this+" to string value");
    }
    public MOOList toList()
    {
        if (this instanceof MOOList)
            return (MOOList)this;
        throw new IllegalStateException("Cannot convert "+getClass().getSimpleName()+"="+this+" to list");
    }
    public MOOMap toMap()
    {
        if (this instanceof MOOMap)
            return (MOOMap)this;
        throw new IllegalStateException("Cannot convert "+getClass().getSimpleName()+"="+this+" to map");
    }
    public MOONumber toNumber()
    {
        if (this instanceof MOONumber)
            return (MOONumber)this;
        throw new IllegalStateException("Cannot convert "+getClass().getSimpleName()+"="+this+" to number");
    }

}