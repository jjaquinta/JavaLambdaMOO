package com.tsatsatzu.moo.core.data.val;

import com.tsatsatzu.moo.core.data.MOOValue;

public class MOOString extends MOOValue
{
    private String  mValue;

    // constructors
    public MOOString()
    {
        mValue = null;
    }
    
    public MOOString(String val)
    {
        mValue = val;
    }
    
    // utilities
    @Override
    public String toString()
    {
        return "\""+mValue+"\"";
    }
    
    // getters and setters

    public String getValue()
    {
        return mValue;
    }

    public void setValue(String value)
    {
        mValue = value;
    }
}
