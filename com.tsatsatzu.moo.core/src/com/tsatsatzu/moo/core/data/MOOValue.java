package com.tsatsatzu.moo.core.data;

import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOMap;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;

import jo.util.utils.obj.BooleanUtils;

public class MOOValue
{
    // utilities
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
