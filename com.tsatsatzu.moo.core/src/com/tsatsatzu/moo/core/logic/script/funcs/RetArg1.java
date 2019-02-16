package com.tsatsatzu.moo.core.logic.script.funcs;

import com.tsatsatzu.moo.core.data.MOOException;

@FunctionalInterface
public interface RetArg1
{
    Object run(Object a0) throws MOOException;
}
