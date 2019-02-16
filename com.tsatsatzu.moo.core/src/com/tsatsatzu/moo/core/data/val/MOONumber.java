package com.tsatsatzu.moo.core.data.val;

import com.tsatsatzu.moo.core.data.MOOValue;

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
