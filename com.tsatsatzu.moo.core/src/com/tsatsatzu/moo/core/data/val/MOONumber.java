package com.tsatsatzu.moo.core.data.val;

import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.MOOValue;

import jo.util.utils.obj.IntegerUtils;

public class MOONumber extends MOOValue
{
    public static final MOONumber TRUE = new MOONumber(1);
    public static final MOONumber FALSE = new MOONumber(0);
    
    private Number  mValue;
    
    // constructors
    public MOONumber()
    {
        mValue = 0;
    }
    
    public MOONumber(int val)
    {
        mValue = val;
    }
    
    public MOONumber(Number val)
    {
        mValue = val;
    }
    
    // utilities

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("type", "num");
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
        return mValue.toString();
    }
    
    // getters and setters

    public Number getValue()
    {
        return mValue;
    }

    public void setValue(Number value)
    {
        mValue = value;
    }
}
