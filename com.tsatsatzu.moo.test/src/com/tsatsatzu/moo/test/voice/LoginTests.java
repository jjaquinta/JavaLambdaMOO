package com.tsatsatzu.moo.test.voice;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.test.VoiceDBBase;

public class LoginTests extends VoiceDBBase
{
    @Test
    public void loginSuccess() throws Exception
    {
        mConnection.startConnection();
        String welcome = mConnection.read();
        Assert.assertEquals("Should be welcome message", "<s>Welcome to Six Mages!</s>", welcome);
        mConnection.write("connect Goddess");
        String loggedIn = mConnection.read();
        Assert.assertEquals("Should be logged in message", "<s>Welcome back Goddess.</s>", loggedIn);
    }
    @Test
    public void loginFail() throws Exception
    {
        mConnection.startConnection();
        String welcome = mConnection.read();
        Assert.assertEquals("Should be welcome message", "<s>Welcome to Six Mages!</s>", welcome);
        mConnection.write("connect Amadan");
        String loggedIn = mConnection.read();
        Assert.assertEquals("Should be failed to log in message", "<s>I don't know the name \"Amadan\".</s>", loggedIn);
    }
    @Test
    public void createNew() throws Exception
    {
        mConnection.startConnection();
        String welcome = mConnection.read();
        Assert.assertEquals("Should be welcome message", "<s>Welcome to Six Mages!</s>", welcome);
        mConnection.write("create Amadan");
        String loggedIn = mConnection.read();
        Assert.assertEquals("Should be successful create message", "<s>Welcome to the game Amadan.</s>", loggedIn);
    }
    @Test
    public void createExisting() throws Exception
    {
        mConnection.startConnection();
        String welcome = mConnection.read();
        Assert.assertEquals("Should be welcome message", "<s>Welcome to Six Mages!</s>", welcome);
        mConnection.write("create Goddess");
        String loggedIn = mConnection.read();
        Assert.assertEquals("Should be successful create message", "<s>The name \"Goddess\" is already taken.</s>", loggedIn);
    }
}
