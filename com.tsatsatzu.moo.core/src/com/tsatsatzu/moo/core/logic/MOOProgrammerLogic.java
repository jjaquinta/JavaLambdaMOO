package com.tsatsatzu.moo.core.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public class MOOProgrammerLogic
{
    private static Map<Thread, Stack<Integer>> mProgrammers = new HashMap<>();
        
    public static void setProgrammer(MOOObjRef programmer)
    {
        synchronized (mProgrammers)
        {
            Stack<Integer> stack = new Stack<>();
            stack.push(programmer.getValue());
            mProgrammers.put(Thread.currentThread(), stack);
        }
    }
    
    public static void clearProgrammer()
    {
        synchronized (mProgrammers)
        {
            mProgrammers.remove(Thread.currentThread());
        }
    }

    public static void pushProgrammer(MOOObjRef programmer)
    {
        synchronized (mProgrammers)
        {
            Stack<Integer> stack = mProgrammers.get(Thread.currentThread());
            if (stack == null)
                setProgrammer(programmer);
            else
                stack.push(programmer.getValue());
        }
    }
    
    public static MOOObjRef popProgrammer() throws MOOException
    {
        synchronized (mProgrammers)
        {
            Stack<Integer> stack = mProgrammers.get(Thread.currentThread());
            if ((stack == null) || (stack.size() == 0))
                throw new MOOException("No active programmer for current thread.");
            int oid = stack.pop();
            return new MOOObjRef(oid);
        }
    }
    
    
    public static MOOObject getProgrammer() throws MOOException
    {
        return MOODbLogic.get(getProgrammerRef());
    }
    
    public static MOOObjRef getProgrammerRef() throws MOOException
    {
        synchronized (mProgrammers)
        {
            Stack<Integer> stack = mProgrammers.get(Thread.currentThread());
            if ((stack == null) || (stack.size() == 0))
                throw new MOOException("No active programmer for current thread.");
            int oid = stack.peek();
            return new MOOObjRef(oid);
        }
    }
}
