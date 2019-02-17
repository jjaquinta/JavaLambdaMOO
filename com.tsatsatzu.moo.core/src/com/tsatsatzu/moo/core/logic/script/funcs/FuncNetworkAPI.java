package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOONetworkAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

public class FuncNetworkAPI
{
    public static void register(ScriptEngine engine)
    {
        engine.put("connected_players", (RetArg1)FuncNetworkAPI::connected_players);
        engine.put("connectedPlayers", (RetArg1)FuncNetworkAPI::connected_players);
        engine.put("connected_seconds", (RetArg1)FuncNetworkAPI::connected_seconds);
        engine.put("connectedSeconds", (RetArg1)FuncNetworkAPI::connected_seconds);
        engine.put("idle_seconds", (RetArg1)FuncNetworkAPI::idle_seconds);
        engine.put("idleSeconds", (RetArg1)FuncNetworkAPI::idle_seconds);
        engine.put("notify", (RetArg3)FuncNetworkAPI::notify);
        engine.put("buffered_output_length", (RetArg1)FuncNetworkAPI::buffered_output_length);
        engine.put("bufferedOutputLength", (RetArg1)FuncNetworkAPI::buffered_output_length);
        engine.put("read", (RetArg2)FuncNetworkAPI::read);
        engine.put("force_input", (VoidArg3)FuncNetworkAPI::force_input);
        engine.put("forceInput", (VoidArg3)FuncNetworkAPI::force_input);
        engine.put("flush_input", (VoidArg2)FuncNetworkAPI::flush_input);
        engine.put("flushInput", (VoidArg2)FuncNetworkAPI::flush_input);
        engine.put("output_delimiters", (RetArg1)FuncNetworkAPI::output_delimiters);
        engine.put("outputDelimiters", (RetArg1)FuncNetworkAPI::output_delimiters);
        engine.put("boot_player", (VoidArg1)FuncNetworkAPI::boot_player);
        engine.put("bootPlayer", (VoidArg1)FuncNetworkAPI::boot_player);
        engine.put("connection_name", (RetArg1)FuncNetworkAPI::connection_name);
        engine.put("connectionName", (RetArg1)FuncNetworkAPI::connection_name);
        engine.put("set_connection_option", (VoidArg3)FuncNetworkAPI::set_connection_option);
        engine.put("setConnectionOption", (VoidArg3)FuncNetworkAPI::set_connection_option);
        engine.put("connection_options", (RetArg1)FuncNetworkAPI::connection_options);
        engine.put("connectionOptions", (RetArg1)FuncNetworkAPI::connection_options);
        engine.put("connection_option", (RetArg2)FuncNetworkAPI::connection_option);
        engine.put("connectionOption", (RetArg2)FuncNetworkAPI::connection_option);
        engine.put("open_network_connection", (RetArg1)FuncNetworkAPI::open_network_connection);
        engine.put("openNetworkConnection", (RetArg1)FuncNetworkAPI::open_network_connection);
        engine.put("listen", (RetArg3)FuncNetworkAPI::listen);
        engine.put("unlisten", (VoidArg1)FuncNetworkAPI::unlisten);
        engine.put("listeners", (RetArg0)FuncNetworkAPI::listeners);
    }
    
    public static Object connected_players(Object arg0) throws MOOException
    {
        MOONumber includeAll = CoerceLogic.toNumber(arg0);
        MOOList ret = MOONetworkAPI.connected_players(includeAll);
        return CoerceLogic.toJavascript(ret);
    }
    
    public static Object connected_seconds(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOONumber ret = MOONetworkAPI.connected_seconds(player);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object idle_seconds(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOONumber ret = MOONetworkAPI.idle_seconds(player);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object notify(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOString msg = CoerceLogic.toString(arg1);
        MOONumber noFlush = CoerceLogic.toNumber(arg2);
        MOONumber ret = MOONetworkAPI.notify(player, msg, noFlush);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object buffered_output_length(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOONumber ret = MOONetworkAPI.buffered_output_length(player);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object read(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOONumber nonBlocking = CoerceLogic.toNumber(arg1);
        MOOString ret = MOONetworkAPI.read(player, nonBlocking);
        return CoerceLogic.toJavascript(ret);
    }

    public static void force_input(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOString line = CoerceLogic.toString(arg1);
        MOONumber nonBlocking = CoerceLogic.toNumber(arg2);
        MOONetworkAPI.force_input(player, line, nonBlocking);
    }

    public static void flush_input(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOONumber showMessages = CoerceLogic.toNumber(arg1);
        MOONetworkAPI.flush_input(player, showMessages);
    }

    public static Object output_delimiters(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOList ret = MOONetworkAPI.output_delimiters(player);
        return CoerceLogic.toJavascript(ret);
    }

    public static void boot_player(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOONetworkAPI.boot_player(player);
    }

    public static Object connection_name(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOString ret = MOONetworkAPI.connection_name(player);
        return CoerceLogic.toJavascript(ret);
    }

    public static void set_connection_option(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOString option = CoerceLogic.toString(arg1);
        MOOValue value = CoerceLogic.toValue(arg2);
        MOONetworkAPI.set_connection_option(player, option, value);
    }

    public static Object connection_options(Object arg0) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOList ret = MOONetworkAPI.connection_options(player);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object connection_option(Object arg0, Object arg1) throws MOOException
    {
        MOOObjRef player = CoerceLogic.toObjRef(arg0);
        MOOString name = CoerceLogic.toString(arg1);
        MOOValue ret = MOONetworkAPI.connection_option(player, name);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object open_network_connection(Object arg0) throws MOOException
    {
        MOOValue value = CoerceLogic.toValue(arg0);
        MOOObjRef ret = MOONetworkAPI.open_network_connection(value);
        return CoerceLogic.toJavascript(ret);
    }

    public static Object listen(Object arg0, Object arg1, Object arg2) throws MOOException
    {
        MOOObjRef object = CoerceLogic.toObjRef(arg0);
        MOOString point = CoerceLogic.toString(arg1);
        MOONumber printMessages = CoerceLogic.toNumber(arg2);
        MOOValue ret = MOONetworkAPI.listen(object, point, printMessages);
        return CoerceLogic.toJavascript(ret);
    }

    public static void unlisten(Object arg0) throws MOOException
    {
        MOOValue canon = CoerceLogic.toValue(arg0);
        MOONetworkAPI.unlisten(canon);
    }

    public static Object listeners() throws MOOException
    {
        MOOList ret = MOONetworkAPI.listeners();
        return CoerceLogic.toJavascript(ret);
    }
}
