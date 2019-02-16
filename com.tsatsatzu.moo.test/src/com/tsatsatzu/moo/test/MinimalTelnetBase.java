package com.tsatsatzu.moo.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;

import com.tsatsatzu.moo.core.api.MOONetworkAPI;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MinimalTelnetBase
{
    protected MOOObjRef      mProgrammer;
    protected MOOValue       mCanon;
    protected Socket         mSocket;
    protected BufferedReader mReader;
    protected BufferedWriter mWriter;

    @Before
    public void setUp() throws Exception
    {
        System.setProperty("moo.store.class",
                "com.tsatsatzu.moo.test.util.MinimalJDBStore");
        MOOOpsLogic.initailize();
        int oid = MOODbLogic.getPlayers()[0];
        mProgrammer = new MOOObjRef(oid);
        MOOProgrammerLogic.pushProgrammer(mProgrammer);
        mCanon = MOONetworkAPI.listen(new MOOObjRef(0), new MOOString("8888"),
                MOONumber.TRUE);
    }

    @After
    public void setDown() throws Exception
    {
        disconnect();
        MOONetworkAPI.unlisten(mCanon);
        MOOProgrammerLogic.popProgrammer();
        MOOOpsLogic.shutdown();
    }

    protected void connect() throws Exception
    {
        System.err.println("Client connecting");
        mSocket = new Socket("127.0.0.1", 8888);
        mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
    }

    public void disconnect() throws IOException
    {        
        if (mSocket != null)
        {
            System.err.println("Client disconnecting");
            mSocket.close();
            mSocket = null;
        }
    }
    
    protected String readLine() throws Exception
    {
        String str = mReader.readLine();
        System.err.println("<"+str);
        return str;
    }
    
    protected void writeLine(String msg) throws Exception
    {
        System.err.println(">"+msg);
        mWriter.write(msg);
        mWriter.newLine();
        mWriter.flush();
    }
}
