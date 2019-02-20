package com.tsatsatzu.moo.core.logic.script.funcs;

import javax.script.ScriptEngine;

import com.tsatsatzu.moo.core.api.MOOAdminAPI;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.CoerceLogic;

import jdk.nashorn.api.scripting.JSObject;

public class FuncAdminAPI
{
    public static void register(ScriptEngine engine)
    {
        JSObject server_version = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString object = MOOAdminAPI.server_version();
                return object;
            }
        };
        JSObject server_log = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString message = CoerceLogic.toString(args[0]);
                MOONumber isError = (args.length > 1)
                        ? CoerceLogic.toNumber(args[1]) : MOONumber.FALSE;
                MOOAdminAPI.server_log(message, isError);
                return null;
            }
        };
        JSObject renumber = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOObjRef object = CoerceLogic.toObjRef(args[0]);
                MOOObjRef ret = MOOAdminAPI.renumber(object);
                return ret;
            }
        };
        JSObject reset_max_object = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOAdminAPI.reset_max_object();
                return null;
            }
        };
        JSObject memory_usage = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOList object = MOOAdminAPI.memory_usage();
                return object;
            }
        };
        JSObject dump_database = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOAdminAPI.dump_database();
                return null;
            }
        };
        JSObject db_disk_size = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOONumber object = MOOAdminAPI.db_disk_size();
                return object;
            }
        };
        JSObject shutdown = new JSFuncObject() {
            @Override
            public MOOValue call(Object... args) throws MOOException
            {
                MOOString message = CoerceLogic.toString(args[0]);
                MOOAdminAPI.shutdown(message);
                return null;
            }
        };
        engine.put("server_version", server_version);
        engine.put("serverVersion", server_version);
        engine.put("server_log", server_log);
        engine.put("serverLog", server_log);
        engine.put("renumber", renumber);
        engine.put("reset_max_object", reset_max_object);
        engine.put("resetMaxObject", reset_max_object);
        engine.put("memory_usage", memory_usage);
        engine.put("memoryUsage", memory_usage);
        engine.put("dump_database", dump_database);
        engine.put("dumpDatabase", dump_database);
        engine.put("db_disk_size", db_disk_size);
        engine.put("dbDiskSize", db_disk_size);
        engine.put("shutdown", shutdown);
    }
}
