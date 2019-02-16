package com.tsatsatzu.moo.core.data;

public interface IConnectionHandler
{
    public boolean isHandlerFor(String spec);
    public MOOConnectionPoint setupPoint(String spec);
}
