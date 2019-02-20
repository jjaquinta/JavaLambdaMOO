package com.tsatsatzu.moo.core.data.val;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.MOOValue;

import jo.audio.util.JSONUtils;

public class MOOMap extends MOOValue
{
    private Map<String, MOOValue>   mValue = new HashMap<>();
    
    // constructors
    public MOOMap()
    {        
    }
    
    // utilities

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("type", "map");
        JSONObject value = new JSONObject();
        json.put("value", value);
        for (String key : mValue.keySet())
        {
            MOOValue v = mValue.get(key);
            value.put(key, v.toJSON());
        }
        return json;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        JSONObject values = JSONUtils.getObject(json, "value");
        for (String key : values.keySet())
        {
            JSONObject v = (JSONObject)values.get(key);
            MOOValue value = MOOValue.newFromJSON(v);
            mValue.put(key, value);
        }
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("{ ");
        for (String key : mValue.keySet())
        {
            if (sb.length() > 1)
                sb.append(", ");
            sb.append("\"");
            sb.append(key);
            sb.append("\": ");
            sb.append(mValue.get(key).toString());
        }
        sb.append(" }");
        return sb.toString();
    }
    
    // getters and setters

    public Map<String, MOOValue> getValue()
    {
        return mValue;
    }

    public void setValue(Map<String, MOOValue> value)
    {
        mValue = value;
    }
}
