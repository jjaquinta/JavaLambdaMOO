package com.tsatsatzu.moo.core.logic.script.funcs;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.AbstractJSObject;

public abstract class JSFuncObject extends AbstractJSObject
{
    @Override
    public Object call(Object thiz, Object... args)
    {
        try
        {
            MOOValue ret = call(args);
            if (ret != null)
                return CoerceLogic.toJavascript(ret);
            else
                return null;
        }
        catch (MOOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    public abstract MOOValue call(Object... args) throws MOOException;
}
