package com.tsatsatzu.moo.core.logic.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.tsatsatzu.moo.core.api.MOOPropertyAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;

import jdk.nashorn.api.scripting.AbstractJSObject;

public class JSObjRef extends AbstractJSObject
{
    private int mOID;
    
    public JSObjRef(int oid)
    {
        mOID = oid;
    }
    
    public JSObjRef(MOOObjRef obj)
    {
        mOID = obj.getValue();
    }
    
    private MOOObject getObject()
    {
        return MOODbLogic.get(mOID);
    }
    
    MOOObjRef getObjRef()
    {
        return new MOOObjRef(mOID);
    }
    
    private boolean isProperty(String name)
    {
        if (mOID < 0)
            return false;
        return getObject().getProperties().containsKey(name);
    }
    
    private boolean isVerb(String name)
    {
        return getObject().getVerb(name) != null;
    }
    
    @Override
    public boolean hasMember(String name)
    {
        return isProperty(name) || isVerb(name);
    }
    
    @Override
    public Object getMember(String name)
    {
        if ("toString".equals(name) || "valueOf".equals(name))
            return "#"+mOID;
        if (mOID < 0)
            return null;
        try
        {
            if (isProperty(name))
            {
                MOOValue val = MOOPropertyAPI.get_property(getObjRef(), new MOOString(name));
                return CoerceLogic.toJavascript(val);
            }
            else
            {
                MOOVerb verb = getObject().getVerb(name);
                if (verb == null)
                    return null;
                return new JSVerb(mOID, name);
            }
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void setMember(String name, Object value)
    {
        try
        {
            MOOValue val = CoerceLogic.toValue(value);
            MOOPropertyAPI.set_property(getObjRef(), new MOOString(name), val);
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void removeMember(String name)
    {
        try
        {
            MOOPropertyAPI.clear_property(getObjRef(), new MOOString(name));
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public Set<String> keySet()
    {
        try
        {
            Set<String> keys = new HashSet<>();
            for (MOOValue v : MOOPropertyAPI.properties(getObjRef()).getValue())
                keys.add(((MOOString)v).getValue());
            for (MOOVerb verb : getObject().getVerbs())
                for (StringTokenizer st = new StringTokenizer(verb.getName(), " "); st.hasMoreTokens(); )
                    keys.add(st.nextToken());
            return keys;
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public Collection<Object> values()
    {
        try
        {
            Collection<Object> values = new ArrayList<>();
            for (MOOValue name : MOOPropertyAPI.properties(getObjRef()).getValue())
            {
                MOOValue val = MOOPropertyAPI.get_property(getObjRef(), (MOOString)name);
                values.add(CoerceLogic.toJavascript(val));
            }
            for (MOOVerb verb : getObject().getVerbs())
                for (StringTokenizer st = new StringTokenizer(verb.getName(), " "); st.hasMoreTokens(); )
                    values.add(new JSVerb(mOID, st.nextToken()));
            return values;
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public Object getDefaultValue(Class<?> hint)
    {
        if (hint == String.class)
            return "#"+mOID;
        else if (hint == Integer.class)
            return mOID;
        return super.getDefaultValue(hint);
    }
}
