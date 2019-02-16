package com.tsatsatzu.moo.core.data;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public class MOOProperty
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
