package com.tsatsatzu.moo.core.logic.script;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.tsatsatzu.moo.core.data.MOOCommand;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncConvAPI;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncNetworkAPI;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncObjectAPI;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncPlayerAPI;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncPropAPI;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncTaskAPI;
import com.tsatsatzu.moo.core.logic.script.funcs.FuncVerbAPI;

public class MOOScriptLogic
{
    private static ScriptEngine mEngine;
    
    private static ScriptEngine getEngine()
    {
        if (mEngine == null)
        {
            ScriptEngineManager m = new ScriptEngineManager();
            mEngine = m.getEngineByName("nashorn");
            FuncPlayerAPI.register(mEngine);
            FuncObjectAPI.register(mEngine);
            FuncPropAPI.register(mEngine);
            FuncVerbAPI.register(mEngine);
            FuncConvAPI.register(mEngine);
            FuncNetworkAPI.register(mEngine);
            FuncTaskAPI.register(mEngine);
        }
        return mEngine;
    }
    public static MOOValue executeScript(MOOObjRef programmer, MOOObject obj, String verbName, Object... args) throws MOOException
    {
        return doExecuteVerb(programmer, obj,  verbName, false, null, args);
    }
    public static MOOValue executeScriptMaybe(MOOObjRef programmer, MOOObject obj, String verbName, Object... args) throws MOOException
    {
        return doExecuteVerb(programmer, obj,  verbName, true, null, args);
    }
    public static MOOValue executeScript(MOOObjRef programmer, MOOObject obj, String verbName, Map<String,Object> props, Object... args) throws MOOException
    {
        return doExecuteVerb(programmer, obj,  verbName, false, props, args);
    }
    private static MOOValue doExecuteVerb(MOOObjRef programmer, MOOObject obj, String verbName, boolean gracefullyDegrade, Map<String,Object> props, Object... args) throws MOOException
    {
        MOOVerb verb = obj.getVerb(verbName);
        if (verb == null)
            if (gracefullyDegrade)
                return null;
            else
                throw new MOOException("No such verb '"+verbName+"' on object #"+obj.getOID());
        String script = verb.getScriptText();
        MOOList argv = (MOOList)CoerceLogic.toValue(args);
        if (props == null)
            props = new HashMap<>();
        props.put("args", argv);
        props.put("_this", new MOOObjRef(obj));
        props.put("verb", new MOOString(verbName));
        props.put("caller", MOOObjRef.NONE);
        props.put("player", new MOOObjRef(programmer));
        try
        {
            return executeScript(programmer, script, props);
        }
        catch (MOOException e)
        {
            throw new MOOException("Error executing #"+obj.getOID()+":"+verbName+" as #"+programmer.getValue(), e);
        }
    }
    public static MOOValue executeCommand(MOOCommand cmd) throws MOOException
    {
        MOOList args = new MOOList();
        for (String arg : cmd.getArgs())
            args.add(arg);
        MOOObjRef player = cmd.getPlayer().toRef();
        String script = cmd.getCmdVerb().getScriptText();
        Map<String,Object> props = new HashMap<>();
        props.put("player", player);
        props.put("_this", cmd.getCmdObj());
        props.put("caller", player);
        props.put("verb", new MOOString(cmd.getVerbStr()));
        props.put("argstr", new MOOString(cmd.getArgstr()));        
        props.put("args", args);
        props.put("dobjstr", new MOOString(cmd.getDObjStr()));
        props.put("dobj", cmd.getDObj());
        props.put("prepstr", new MOOString(cmd.getPrepStr()));
        props.put("iobjstr", new MOOString(cmd.getIObjStr()));
        props.put("iobj", cmd.getIObj());
        return executeScript(player, script, props);
    }
    public static MOOValue executeScript(MOOObjRef programmer, String script, Object... args) throws MOOException
    {
        MOOList argv = (MOOList)CoerceLogic.toValue(args);
        Map<String,Object> props = new HashMap<>();
        props.put("args", argv);
        return executeScript(programmer, script, props);
    }
    public static MOOValue executeScript(MOOObjRef programmer, String script, Map<String,Object> props) throws MOOException
    {
        script = preProcess(script);
        addConstants(props);
        MOOProgrammerLogic.pushProgrammer(programmer);
        try
        {
            ScriptEngine e = getEngine();
            ScriptContext c = addPropsToScope(e, props);
            Object ret = e.eval(script, c);
            MOOValue val = CoerceLogic.toValue(ret);
            return val;
        }
        catch (Exception e)
        {
            throw new MOOException("Error executing script", e);
        }
        finally
        {
            MOOProgrammerLogic.popProgrammer();
        }
    }

    private static ScriptContext addPropsToScope(ScriptEngine e,
            Map<String, Object> props)
    {
        ScriptContext context = e.getContext();
        Bindings b = context.getBindings(ScriptContext.ENGINE_SCOPE);
        for (String key : props.keySet())
        {
            Object val = props.get(key);
            if (val instanceof MOOValue)
                val = CoerceLogic.toJavascript((MOOValue)val);
            b.put(key, val);
        }
        return context;
    }
    
    private static String preProcess(String script)
    {
        StringBuffer sb = new StringBuffer();
        int state = 0;
        for (int i = 0; i < script.length(); i++)
        {
            char ch = script.charAt(i);
            switch (state)
            {
                case 0:
                    if (ch == '$')
                        sb.append("toobj(0).");
                    else if (ch == '#')
                    {
                        sb.append("toobj(");
                        while ((i + 1 < script.length()) && Character.isDigit(script.charAt(i+1)))
                        {
                            sb.append(script.charAt(++i));
                        }
                        sb.append(")");
                    }
                    else if (ch == '\"')
                    {
                        sb.append(ch);
                        state = 1;
                    }
                    else if (ch == '\'')
                    {
                        sb.append(ch);
                        state = 2;
                    }
                    else
                        sb.append(ch);
                    break;
                case 1:
                    if (ch == '\"')
                    {
                        sb.append(ch);
                        state = 0;
                    }
                    else if (ch == '\\')
                    {
                        sb.append(ch);
                        sb.append(script.charAt(++i));
                    }
                    else
                        sb.append(ch);
                    break;
                case 2:
                    if (ch == '\'')
                    {
                        sb.append(ch);
                        state = 0;
                    }
                    else if (ch == '\\')
                    {
                        sb.append(ch);
                        sb.append(script.charAt(++i));
                    }
                    else
                        sb.append(ch);
                    break;
            }
        }
        return sb.toString();
    }
    
    private static void addConstants(Map<String,Object> props)
    {
        props.put("E_NONE", 0);
        props.put("E_TYPE", 1); // Type mismatch
        props.put("E_DIV", 2); // Division by zero
        props.put("E_PERM", 3); // Permission denied
        props.put("E_PROPNF", 4); // Property not found
        props.put("E_VERBNF", 5); // Verb not found
        props.put("E_VARNF", 6); // Variable not found
        props.put("E_INVIND", 7); // Invalid indirection
        props.put("E_RECMOVE", 8); // Recursive move
        props.put("E_MAXREC", 9); // Too many verb calls
        props.put("E_RANGE", 10); // Range error
        props.put("E_ARGS", 11); // Incorrect number of arguments
        props.put("E_NACC", 12); // Move refused by destination
        props.put("E_INVARG", 13); // Invalid argument
        props.put("E_QUOTA", 14); // Resource limit exceeded
        props.put("E_FLOAT", 15); // Floating-point arithmetic error
    }
}
