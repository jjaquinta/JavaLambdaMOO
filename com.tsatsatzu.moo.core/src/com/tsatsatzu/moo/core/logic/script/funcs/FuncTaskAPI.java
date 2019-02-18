package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOTaskAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class FuncTaskAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("raise", (VoidArg3)FuncTaskAPI::raise);
        engine.put("call_function", (RetArg2)FuncTaskAPI::call_function);
        engine.put("function_info", (RetArg1)FuncTaskAPI::function_info);
        engine.put("eval", (RetArg1)FuncTaskAPI::eval);
        engine.put("set_task_perms", (VoidArg1)FuncTaskAPI::set_task_perms);
        engine.put("caller_perms", (RetArg0)FuncTaskAPI::caller_perms);
        engine.put("ticks_left", (RetArg0)FuncTaskAPI::ticks_left);
        engine.put("seconds_left", (RetArg0)FuncTaskAPI::seconds_left);
        engine.put("task_id", (RetArg0)FuncTaskAPI::task_id);
        engine.put("suspend", (RetArg1)FuncTaskAPI::suspend);
        engine.put("resume", (VoidArg2)FuncTaskAPI::resume);
        engine.put("queue_info", (RetArg1)FuncTaskAPI::queue_info);
        engine.put("queued_tasks", (RetArg0)FuncTaskAPI::queued_tasks);
        engine.put("kill_task", (VoidArg1)FuncTaskAPI::kill_task);
        engine.put("callers", (RetArg1)FuncTaskAPI::callers);
        engine.put("task_stack", (RetArg2)FuncTaskAPI::task_stack);
    }
    
    public static void raise(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOString code = CoerceLogic.toString(arg0); 
        MOOString message = CoerceLogic.toString(arg1); 
        MOOValue value = CoerceLogic.toValue(arg2);
        MOOTaskAPI.raise(code, message, value);
    }
    public static Object call_function(Object arg0, Object arg1) throws MOOException
    {
        MOOString funcName = CoerceLogic.toString(arg0);
        MOOValue arg = CoerceLogic.toValue(arg1);
        MOOValue ret = MOOTaskAPI.call_function(funcName, arg);
        return CoerceLogic.toJavascript(ret);
    }
    public static Object function_info(Object arg0) throws MOOException
    {
        MOOString name = CoerceLogic.toString(arg0);
        MOOList ret = MOOTaskAPI.function_info(name);
        return CoerceLogic.toJavascript(ret);
    }
    public static Object eval(Object arg0) throws MOOException
    {
        MOOString code = CoerceLogic.toString(arg0);
        MOOList ret = MOOTaskAPI.eval(code);
        return CoerceLogic.toJavascript(ret);
    }
    public static void set_task_perms(Object arg0) throws MOOException
    {
        MOOObjRef who = CoerceLogic.toObjRef(arg0);
        MOOTaskAPI.set_task_perms(who);
    }
    public static Object caller_perms() throws MOOException
    {
        MOOObjRef ret = MOOTaskAPI.caller_perms();
        return CoerceLogic.toJavascript(ret);
    }
    public static Object ticks_left() throws MOOException
    {
        MOONumber ret = MOOTaskAPI.ticks_left();
        return CoerceLogic.toJavascript(ret);
    }
    public static Object seconds_left() throws MOOException
    {
        MOONumber ret = MOOTaskAPI.seconds_left();
        return CoerceLogic.toJavascript(ret);
    }
    public static Object task_id() throws MOOException
    {
        MOONumber ret = MOOTaskAPI.task_id();
        return CoerceLogic.toJavascript(ret);
    }
    public static Object suspend(Object arg0) throws MOOException
    {
        MOONumber seconds = CoerceLogic.toNumber(arg0);
        MOOValue ret = MOOTaskAPI.suspend(seconds);
        return CoerceLogic.toJavascript(ret);
    }
    public static void resume(Object arg0, Object arg1) throws MOOException
    {
        MOONumber taskID = CoerceLogic.toNumber(arg0);
        MOOValue value = CoerceLogic.toValue(arg1);
        MOOTaskAPI.resume(taskID, value);
    }
    public static Object queue_info(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOList ret = MOOTaskAPI.queue_info(player);
        return CoerceLogic.toJavascript(ret);
    }
    public static Object queued_tasks() throws MOOException
    {
        MOOList ret = MOOTaskAPI.queued_tasks();
        return CoerceLogic.toJavascript(ret);
    }
    public static void kill_task(Object arg0) throws MOOException
    {
        MOONumber includeAll = CoerceLogic.toNumber(arg0);
        MOOTaskAPI.kill_task(includeAll);
    }
    public static Object callers(Object arg0) throws MOOException
    {
        MOONumber includeLineNumbers = CoerceLogic.toNumber(arg0);
        MOOList ret = MOOTaskAPI.callers(includeLineNumbers);
        return CoerceLogic.toJavascript(ret);
    }
    public static Object task_stack(Object arg0, Object arg1) throws MOOException
    {
        MOONumber taskID = CoerceLogic.toNumber(arg0);
        MOONumber inlcudeLineNumbers = CoerceLogic.toNumber(arg1);
        MOOList ret = MOOTaskAPI.task_stack(taskID, inlcudeLineNumbers);
        return CoerceLogic.toJavascript(ret);
    }
}
