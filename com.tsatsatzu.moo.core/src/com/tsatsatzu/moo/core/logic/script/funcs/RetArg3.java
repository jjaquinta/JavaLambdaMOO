package com.tsatsatzu.moo.core.logic.script.funcs;

import com.tsatsatzu.moo.core.data.MOOException;

@FunctionalInterface
public interface RetArg3
{
    Object run(Object a0, Object a1, Object a2) throws MOOException;
}
