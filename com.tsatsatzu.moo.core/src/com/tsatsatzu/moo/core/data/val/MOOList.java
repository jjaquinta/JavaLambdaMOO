package com.tsatsatzu.moo.core.data.val;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.MOOValue;

import jo.audio.util.JSONUtils;

public class MOOList extends MOOValue
{
    private List<MOOValue> mValue = new ArrayList<MOOValue>();
    
    // constructors
    public MOOList()
    {        
    }
    
    public MOOList(Object... vals)
    {        
        add(vals);
    }
    
    // utilities

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("type", "list");
        JSONArray value = new JSONArray();
        json.put("value", value);
        for (MOOValue v : mValue)
            value.add(v.toJSON());
        return json;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        JSONArray values = JSONUtils.getArray(json, "value");
        for (int i = 0; i < values.size(); i++)
        {
            JSONObject v = (JSONObject)values.get(i);
            MOOValue value = MOOValue.newFromJSON(v);
            mValue.add(value);
        }
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < mValue.size(); i++)
        {
            if (i > 0)
                sb.append(",");
            sb.append(" ");
            sb.append(mValue.get(i).toString());
        }
        sb.append(" ]");
        return sb.toString();
    }
    
    public int size()
    {
        return mValue.size();
    }
    
    public MOOValue get(int idx)
    {
        return mValue.get(idx);
    }
    
    public void add(Object... vals)
    {
        for (Object v : vals)
            if (v instanceof MOOValue)
                mValue.add((MOOValue)v);
            else if (v instanceof Number)
                mValue.add(new MOONumber((Number)v));
            else if (v instanceof String)
                mValue.add(new MOOString((String)v));
    }
    
    // getters and setters

    public List<MOOValue> getValue()
    {
        return mValue;
    }

    public void setValue(List<MOOValue> value)
    {
        mValue = value;
    }
}
