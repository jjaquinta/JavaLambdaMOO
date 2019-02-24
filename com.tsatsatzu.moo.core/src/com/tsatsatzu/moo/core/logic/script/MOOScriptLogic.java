package com.tsatsatzu.moo.core.logic.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.tsatsatzu.moo.core.data.MOOCommand;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOStackElement;
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
    private static final String LANGUAGE_DEFAULT = "javascript";
    private static final String LANGUAGE_PREFIX = "${";
    private static final String LANGUAGE_SUFFIX = "}";
    
    private static Map<String,String> mLanguageToEngine = new HashMap<>();
    static
    {
        mLanguageToEngine.put("javascript", "nashorn");
        mLanguageToEngine.put("python", "jython");
    }
    private static Map<String,ScriptEngine> mEngines = new HashMap<>();
    private static Map<Thread, Stack<MOOStackElement>> mScriptStacks = new HashMap<Thread, Stack<MOOStackElement>>();
    
    private static ScriptEngine getEngine(String language)
    {
        if (!mEngines.containsKey(language))
        {
            String engineName = mLanguageToEngine.get(language);
            ScriptEngineManager m = new ScriptEngineManager();
            ScriptEngine engine = m.getEngineByName(engineName);
            FuncPlayerAPI.register(engine);
            FuncObjectAPI.register(engine);
            FuncPropAPI.register(engine);
            FuncVerbAPI.register(engine);
            FuncConvAPI.register(engine);
            FuncNetworkAPI.register(engine);
            FuncTaskAPI.register(engine);
            mEngines.put(language, engine);
        }
        return mEngines.get(language);
    }
    public static MOOValue executeScript(MOOObjRef player, MOOObject obj, String verbName, Object... args) throws MOOException
    {
        return doExecuteVerb(player, obj,  verbName, false, null, args);
    }
    public static MOOValue executeScriptMaybe(MOOObjRef player, MOOObject obj, String verbName, Object... args) throws MOOException
    {
        return doExecuteVerb(player, obj,  verbName, true, null, args);
    }
    public static MOOValue executeScript(MOOObjRef player, MOOObject obj, String verbName, Map<String,Object> props, Object... args) throws MOOException
    {
        return doExecuteVerb(player, obj, verbName, false, props, args);
    }
    private static MOOValue doExecuteVerb(MOOObjRef player, MOOObject obj, String verbName, boolean gracefullyDegrade, Map<String,Object> props, Object... args) throws MOOException
    {
        if (player == null)
        {
            MOOStackElement mse = peekScriptStack();
            if (mse == null)
                throw new MOOException("No player to be found!");
            player = new MOOObjRef(mse.getPlayer());
        }
        MOOObjRef programmer = obj.getOwner();
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
        props.put("_caller", MOOObjRef.NONE);
        props.put("_player", player);
        MOOStackElement st = new MOOStackElement();
        st.setThis(obj.getOID());
        st.setVerbName(verbName);
        st.setProgrammer(programmer.getValue());
        st.setVerbLoc(obj.getOID());
        st.setPlayer(programmer.getValue());
        st.setLineNumber(0);
        pushScriptStack(st);
        MOOProgrammerLogic.pushProgrammer(programmer);
        try
        {
            return executeScript(script, props);
        }
        catch (MOOException e)
        {
            throw new MOOException("Error executing #"+obj.getOID()+":"+verbName+" as #"+programmer.getValue(), e);
        }
        finally
        {
            MOOProgrammerLogic.popProgrammer();
            popScriptStack();
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
        props.put("_player", player);
        props.put("_this", cmd.getCmdObj());
        props.put("_caller", player);
        props.put("verb", new MOOString(cmd.getVerbStr()));
        props.put("argstr", new MOOString(cmd.getArgstr()));        
        props.put("args", args);
        props.put("dobjstr", new MOOString(cmd.getDObjStr()));
        props.put("dobj", cmd.getDObj());
        props.put("prepstr", new MOOString(cmd.getPrepStr()));
        props.put("iobjstr", new MOOString(cmd.getIObjStr()));
        props.put("iobj", cmd.getIObj());
        MOOProgrammerLogic.pushProgrammer(player);
        try
        {
            return executeScript(script, props);
        }
        finally
        {
            MOOProgrammerLogic.popProgrammer();
        }
    }
    public static MOOValue executeScript(String script, Object... args) throws MOOException
    {
        MOOList argv = (MOOList)CoerceLogic.toValue(args);
        Map<String,Object> props = new HashMap<>();
        props.put("args", argv);
        return executeScript(script, props);
    }
    public static MOOValue executeScript(String script, Map<String,Object> props) throws MOOException
    {
        String language = getLanguage(script);
        script = preProcess(script);
        //System.err.println(script);
        addConstants(props);
        try
        {
            ScriptEngine e = getEngine(language);
            ScriptContext c = addPropsToScope(e, props);
            Object ret = e.eval(script, c);
            MOOValue val = CoerceLogic.toValue(ret);
            return val;
        }
        catch (Exception e)
        {
            throw new MOOException("Error executing script", e);
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
        // strip language identifiers
        for (;;)
        {
            int o = script.indexOf(LANGUAGE_PREFIX);
            if (o < 0)
                break;
            int e = script.indexOf(LANGUAGE_SUFFIX, o + LANGUAGE_PREFIX.length());
            if (e < 0)
                break;
            script = script.substring(0, o) + script.substring(e + LANGUAGE_SUFFIX.length());
        }
        // parse for $/#
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
                    else if (ch == '/')
                    {
                        sb.append(ch);
                        if (script.charAt(i+1) == '/')
                            state = 3;
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
                case 3:
                    sb.append(ch);
                    if (ch == '\n')
                        state = 0;
                    break;
            }
        }
        return sb.toString();
    }
    
    private static String getLanguage(String script)
    {
        int o = script.indexOf(LANGUAGE_PREFIX);
        if (o < 0)
            return LANGUAGE_DEFAULT;
        int e = script.indexOf(LANGUAGE_SUFFIX, o + LANGUAGE_PREFIX.length());
        if (e < 0)
            return LANGUAGE_DEFAULT;
        String language = script.substring(o + LANGUAGE_PREFIX.length(), e);
        return language;
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
        props.put("STR", 2); // String
        props.put("NUM", 0); // Number
        props.put("OBJ", 1); // Object
        props.put("LIST", 4); // List
    }
    
    private static void pushScriptStack(MOOStackElement ele)
    {
        Stack<MOOStackElement> s = mScriptStacks.get(Thread.currentThread());
        if (s == null)
        {
            s = new Stack<>();
            mScriptStacks.put(Thread.currentThread(), s);
        }
        s.push(ele);
    }
    
    private static void popScriptStack()
    {
        Stack<MOOStackElement> s = mScriptStacks.get(Thread.currentThread());
        if (s != null)
        {
            s.pop();
            if (s.size() == 0)
                mScriptStacks.remove(Thread.currentThread());
        }
    }
    
    private static MOOStackElement peekScriptStack()
    {
        Stack<MOOStackElement> s = mScriptStacks.get(Thread.currentThread());
        if (s != null)
        {
            if (s.size() == 0)
                return null;
            else
                return s.peek();
        }
        return null;
    }
    
    public static MOOStackElement[] getScriptStack()
    {
        Stack<MOOStackElement> s = mScriptStacks.get(Thread.currentThread());
        if (s == null)
            return new MOOStackElement[0];
        else
            return s.toArray(new MOOStackElement[0]);
    }
}
