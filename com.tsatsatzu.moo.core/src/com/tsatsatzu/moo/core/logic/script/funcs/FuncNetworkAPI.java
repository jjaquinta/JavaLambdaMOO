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

import jdk.nashorn.api.scripting.JSObject;

public class FuncNetworkAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject connected_players = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber includeAll = CoerceLogic.toNumber(args[0]);
                MOOList ret = MOONetworkAPI.connected_players(includeAll);
                return ret;
            }
        }; 
        JSObject connected_seconds = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOONumber ret = MOONetworkAPI.connected_seconds(player);
                return ret;
            }
        }; 
        JSObject idle_seconds = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOONumber ret = MOONetworkAPI.idle_seconds(player);
                return ret;
            }
        }; 
        JSObject notify = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOString msg = CoerceLogic.toString(args[1]);
                MOONumber noFlush = CoerceLogic.toNumber(args[2]);
                MOONumber ret = MOONetworkAPI.notify(player, msg, noFlush);
                return ret;
            }
        }; 
        JSObject buffered_output_length = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOONumber ret = MOONetworkAPI.buffered_output_length(player);
                return ret;
            }
        }; 
        JSObject read = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOONumber nonBlocking = CoerceLogic.toNumber(args[1]);
                MOOString ret = MOONetworkAPI.read(player, nonBlocking);
                return ret;
            }
        }; 
        JSObject force_input = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOString line = CoerceLogic.toString(args[1]);
                MOONumber nonBlocking = CoerceLogic.toNumber(args[2]);
                MOONetworkAPI.force_input(player, line, nonBlocking);
                return null;
            }
        }; 
        JSObject flush_input = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOONumber showMessages = CoerceLogic.toNumber(args[1]);
                MOONetworkAPI.flush_input(player, showMessages);
                return null;
            }
        }; 
        JSObject output_delimiters = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOList ret = MOONetworkAPI.output_delimiters(player);
                return ret;
            }
        }; 
        JSObject boot_player = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOONetworkAPI.boot_player(player);
                return null;
            }
        }; 
        JSObject connection_name = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOString ret = MOONetworkAPI.connection_name(player);
                return ret;
            }
        }; 
        JSObject set_connection_option = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOString option = CoerceLogic.toString(args[1]);
                MOOValue value = CoerceLogic.toValue(args[2]);
                MOONetworkAPI.set_connection_option(player, option, value);
                return null;
            }
        }; 
        JSObject connection_options = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOList ret = MOONetworkAPI.connection_options(player);
                return ret;
            }
        }; 
        JSObject connection_option = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef player = CoerceLogic.toObjRef(args[0]);
                MOOString name = CoerceLogic.toString(args[1]);
                MOOValue ret = MOONetworkAPI.connection_option(player, name);
                return ret;
            }
        }; 
        JSObject open_network_connection = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOValue value = CoerceLogic.toValue(args[0]);
                MOOObjRef ret = MOONetworkAPI.open_network_connection(value);
                return ret;
            }
        }; 
        JSObject listen = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOString point = CoerceLogic.toString(args[1]);
                MOONumber printMessages = CoerceLogic.toNumber(args[2]);
                MOOValue ret = MOONetworkAPI.listen(object, point, printMessages);
                return ret;
            }
        }; 
        JSObject unlisten = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOValue canon = CoerceLogic.toValue(args[0]);
                MOONetworkAPI.unlisten(canon);
                return null;
            }
        }; 
        JSObject listeners = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOList ret = MOONetworkAPI.listeners();
                return ret;
            }
        }; 
        engine.put("connected_players", connected_players);
        engine.put("connectedPlayers", connected_players);
        engine.put("connected_seconds", connected_seconds);
        engine.put("connectedSeconds", connected_seconds);
        engine.put("idle_seconds", idle_seconds);
        engine.put("idleSeconds", idle_seconds);
        engine.put("notify", notify);
        engine.put("buffered_output_length", buffered_output_length);
        engine.put("bufferedOutputLength", buffered_output_length);
        engine.put("read", read);
        engine.put("force_input", force_input);
        engine.put("forceInput", force_input);
        engine.put("flush_input", flush_input);
        engine.put("flushInput", flush_input);
        engine.put("output_delimiters", output_delimiters);
        engine.put("outputDelimiters", output_delimiters);
        engine.put("boot_player", boot_player);
        engine.put("bootPlayer", boot_player);
        engine.put("connection_name", connection_name);
        engine.put("connectionName", connection_name);
        engine.put("set_connection_option", set_connection_option);
        engine.put("setConnectionOption", set_connection_option);
        engine.put("connection_options", connection_options);
        engine.put("connectionOptions", connection_options);
        engine.put("connection_option", connection_option);
        engine.put("connectionOption", connection_option);
        engine.put("open_network_connection", open_network_connection);
        engine.put("openNetworkConnection", open_network_connection);
        engine.put("listen", listen);
        engine.put("unlisten", unlisten);
        engine.put("listeners", listeners);
    }
}
