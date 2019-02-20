package com.tsatsatzu.moo.core.data;

import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

import jo.audio.util.IJSONAble;
import jo.audio.util.JSONUtils;

public class MOOProperty implements IJSONAble
{
    private String      mName;
    private boolean     mClear;
    private boolean     mDefinition;
    private MOOValue    mValue;
    private MOOObjRef   mOwner;
    private boolean     mRead;
    private boolean     mWrite;
    private boolean     mChange;
    
    // constructors
    public MOOProperty()
    {        
    }
    
    public MOOProperty(MOOProperty prop)
    {
        mName = prop.mName;
        mClear = true;
        mDefinition = false;
        mValue = null;
        mOwner = prop.mOwner;
        mRead = prop.mRead;
        mWrite = prop.mWrite;
        mChange = prop.mChange;
    }
    // utilities
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", mName);
        json.put("clear", mClear);
        json.put("definition", mDefinition);
        json.put("value", MOOValue.toJSON(mValue));
        json.put("owner", mOwner.getValue());
        json.put("read", mRead);
        json.put("write", mWrite);
        json.put("change", mChange);
        return json;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        mName = JSONUtils.getString(json, "name");
        mClear = JSONUtils.getBoolean(json, "clear");
        mDefinition = JSONUtils.getBoolean(json, "definition");
        mValue = MOOValue.newFromJSON(JSONUtils.getObject(json, "value"));
        mOwner = new MOOObjRef(JSONUtils.getInt(json, "owner"));
        mRead = JSONUtils.getBoolean(json, "read");
        mWrite = JSONUtils.getBoolean(json, "write");
        mChange = JSONUtils.getBoolean(json, "change");
    }

    
    // gettters and setters
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public MOOValue getValue()
    {
        return mValue;
    }
    public void setValue(MOOValue value)
    {
        mValue = value;
    }
    public MOOObjRef getOwner()
    {
        return mOwner;
    }
    public void setOwner(MOOObjRef owner)
    {
        mOwner = owner;
    }
    public boolean isRead()
    {
        return mRead;
    }
    public void setRead(boolean read)
    {
        mRead = read;
    }
    public boolean isWrite()
    {
        return mWrite;
    }
    public void setWrite(boolean write)
    {
        mWrite = write;
    }
    public boolean isChange()
    {
        return mChange;
    }
    public void setChange(boolean change)
    {
        mChange = change;
    }
    public boolean isClear()
    {
        return mClear;
    }
    public void setClear(boolean clear)
    {
        mClear = clear;
    }

    public boolean isDefinition()
    {
        return mDefinition;
    }

    public void setDefinition(boolean definition)
    {
        mDefinition = definition;
    }
}
