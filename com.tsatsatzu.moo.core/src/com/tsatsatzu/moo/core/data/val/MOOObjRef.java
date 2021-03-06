package com.tsatsatzu.moo.core.data.val;

import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;

import jo.util.utils.obj.IntegerUtils;

public class MOOObjRef extends MOOValue
{
    public static final MOOObjRef NONE = new MOOObjRef(-1);
    
    private int mValue;

    // constructors
    public MOOObjRef()
    {
        mValue = -1;
    }
    
    public MOOObjRef(int val)
    {
        mValue = val;
    }
    
    public MOOObjRef(MOOObjRef ref)
    {
        mValue = ref.getValue();
    }
    
    public MOOObjRef(MOOObject ref)
    {
        mValue = ref.getOID();
    }
    
    // utilities

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("type", "obj");
        json.put("value", mValue);
        return json;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        mValue = IntegerUtils.parseInt(json.get("value"));
    }

    @Override
    public String toString()
    {
        return "#"+mValue;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MOOObjRef)
            return ((MOOObjRef)obj).getValue() == mValue;
        else if (obj instanceof MOOObject)
            return ((MOOObject)obj).getOID() == mValue;
        else if (obj instanceof Number)
            return ((Number)obj).intValue() == mValue;
        else if (obj == null)
            return -1 == mValue;
        return false;
    }
    
    public boolean isNone()
    {
        return mValue == -1;
    }
    
    // getters and setters

    public int getValue()
    {
        return mValue;
    }

    public void setValue(int reference)
    {
        mValue = reference;
    }
}
