package com.tsatsatzu.moo.core.logic;

import com.tsatsatzu.moo.core.data.IConnectionHandler;
import com.tsatsatzu.moo.core.data.MOOConnectionPoint;
import com.tsatsatzu.moo.core.data.MOOTCPIPConnectionPoint;

import jo.util.utils.obj.IntegerUtils;

public class TCPIPConnectionHandler implements IConnectionHandler
{
    @Override
    public boolean isHandlerFor(String spec)
    {
        return spec.startsWith("tcpip://") || (IntegerUtils.parseInt(spec) > 0);
    }

    @Override
    public MOOConnectionPoint setupPoint(String spec)
    {
        if (spec.startsWith("tcpip://"))
            spec = spec.substring(8);
        int port = IntegerUtils.parseInt(spec);
        MOOConnectionPoint conn = new MOOTCPIPConnectionPoint();
        conn.setCanon(port);
        return conn;
    }

}
