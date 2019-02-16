package com.tsatsatzu.moo.core.logic.script.funcs;

import com.tsatsatzu.moo.core.data.MOOException;

@FunctionalInterface
public interface RetArg2
{
    Object run(Object a0, Object a1) throws MOOException;
}
