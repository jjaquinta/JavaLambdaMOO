package com.tsatsatzu.moo.test.voice;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.test.VoiceDBBase;

public class LookTests extends VoiceDBBase
{
    @Test
    public void look() throws Exception
    {
        mConnection.startConnection();
        String welcome = mConnection.read();
        Assert.assertEquals("Should be welcome message", "<s>Welcome to Six Mages!</s>", welcome);
        mConnection.write("create Amadan");
        String loggedIn = mConnection.read();
        Assert.assertEquals("Should be successful create message", "<s>Welcome to the game Amadan.</s>", loggedIn);
        String room = mConnection.read();
        Assert.assertEquals("Should be room description", "<s>This is an elegant foyeur of white marble.</s>", room);
        mConnection.write("look");
        room = mConnection.read();
        Assert.assertEquals("Should be room description", "<s>This is an elegant foyeur of white marble.</s>", room);        
    }
}
