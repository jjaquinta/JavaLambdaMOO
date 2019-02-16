package com.tsatsatzu.moo.core.logic.script.funcs;

import com.tsatsatzu.moo.core.data.MOOException;

@FunctionalInterface
public interface VoidArg3
{
    void run(Object a0, Object a1, Object a2) throws MOOException;
}
