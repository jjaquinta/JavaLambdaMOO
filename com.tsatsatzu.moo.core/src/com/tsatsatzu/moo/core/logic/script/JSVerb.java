package com.tsatsatzu.moo.core.logic.script;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.logic.MOODbLogic;

import jdk.nashorn.api.scripting.AbstractJSObject;

public class JSVerb extends AbstractJSObject
{
    private int mOID;
    private String mVerbName;
    
    public JSVerb(int oid, String verb)
    {
        mOID = oid;
        mVerbName = verb;
    }
    
    @Override
    public boolean isFunction()
    {
        return true;
    }
    
    @Override
    public Object call(Object thiz, Object... args)
    {
        try
        {
            MOOObject obj = MOODbLogic.get(mOID);
            MOOValue ret = MOOScriptLogic.executeScript(null, obj, mVerbName, args);
            return CoerceLogic.toJavascript(ret);
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
