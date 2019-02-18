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

import jdk.nashorn.api.scripting.JSObject;

public class FuncTaskAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject raise = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString code = CoerceLogic.toString(args[0]); 
                MOOString message = CoerceLogic.toString(args[1]); 
                MOOValue value = CoerceLogic.toValue(args[2]);
                MOOTaskAPI.raise(code, message, value);
                return null;
            }
        }; 
        JSObject call_function = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString funcName = CoerceLogic.toString(args[0]);
                MOOValue arg = CoerceLogic.toValue(args[1]);
                MOOValue ret = MOOTaskAPI.call_function(funcName, arg);
                return ret;
            }
        }; 
        JSObject function_info = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString name = CoerceLogic.toString(args[0]);
                MOOList ret = MOOTaskAPI.function_info(name);
                return ret;
            }
        }; 
        JSObject eval = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString code = CoerceLogic.toString(args[0]);
                MOOList ret = MOOTaskAPI.eval(code);
                return ret;
            }
        }; 
        JSObject set_task_perms = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef who = CoerceLogic.toObjRef(args[0]);
                MOOTaskAPI.set_task_perms(who);
                return null;
            }
        }; 
        JSObject caller_perms = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef ret = MOOTaskAPI.caller_perms();
                return ret;
            }
        }; 
        JSObject ticks_left = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber ret = MOOTaskAPI.ticks_left();
                return ret;
            }
        }; 
        JSObject seconds_left = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber ret = MOOTaskAPI.seconds_left();
                return ret;
            }
        }; 
        JSObject task_id = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber ret = MOOTaskAPI.task_id();
                return ret;
            }
        }; 
        JSObject suspend = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber seconds = CoerceLogic.toNumber(args[0]);
                MOOValue ret = MOOTaskAPI.suspend(seconds);
                return ret;
            }
        }; 
        JSObject resume = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber taskID = CoerceLogic.toNumber(args[0]);
                MOOValue value = CoerceLogic.toValue(args[1]);
                MOOTaskAPI.resume(taskID, value);
                return null;
            }
        }; 
        JSObject queue_info = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOList ret = MOOTaskAPI.queue_info(player);
                return ret;
            }
        }; 
        JSObject queued_tasks = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOList ret = MOOTaskAPI.queued_tasks();
                return ret;
            }
        }; 
        JSObject kill_task = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber includeAll = CoerceLogic.toNumber(args[0]);
                MOOTaskAPI.kill_task(includeAll);
                return null;
            }
        }; 
        JSObject callers = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber includeLineNumbers = CoerceLogic.toNumber(args[0]);
                MOOList ret = MOOTaskAPI.callers(includeLineNumbers);
                return ret;
            }
        }; 
        JSObject task_stack = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber taskID = CoerceLogic.toNumber(args[0]);
                MOONumber inlcudeLineNumbers = CoerceLogic.toNumber(args[1]);
                MOOList ret = MOOTaskAPI.task_stack(taskID, inlcudeLineNumbers);
                return ret;
            }
        }; 

        
        engine.put("raise", raise);
        engine.put("call_function", call_function);
        engine.put("function_info", function_info);
        engine.put("eval", eval);
        engine.put("set_task_perms", set_task_perms);
        engine.put("caller_perms", caller_perms);
        engine.put("ticks_left", ticks_left);
        engine.put("seconds_left", seconds_left);
        engine.put("task_id", task_id);
        engine.put("suspend", suspend);
        engine.put("resume", resume);
        engine.put("queue_info", queue_info);
        engine.put("queued_tasks", queued_tasks);
        engine.put("kill_task", kill_task);
        engine.put("callers", callers);
        engine.put("task_stack", task_stack);
    }
}
