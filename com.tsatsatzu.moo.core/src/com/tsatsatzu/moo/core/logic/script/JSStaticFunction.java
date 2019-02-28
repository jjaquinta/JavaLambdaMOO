package com.tsatsatzu.moo.core.logic.script;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

import jdk.nashorn.api.scripting.AbstractJSObject;

public class JSStaticFunction extends AbstractJSObject
{
    private int mOID;
    private String mVerbName;
    
    public JSStaticFunction(int oid, String verb)
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
        if ("equals".equals(mVerbName))
            return callEquals(thiz, args);
        else
            throw new IllegalStateException("Unknown static function '"+mVerbName+"'");
    }
    
    private Object callEquals(Object thiz, Object... args)
    {
        if (args.length != 1)
            throw new IllegalStateException("Expected 1 arguments for '"+mVerbName+"', not "+args.length);
        MOOObjRef o2 = CoerceLogic.toObjRef(args[0]);
        return mOID == o2.getValue();
    }
}
