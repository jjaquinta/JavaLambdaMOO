package com.tsatsatzu.moo.test.core;

import org.junit.Assert;
import org.junit.Test;

import com.tsatsatzu.moo.test.CoreDBBase;

public class LoginTests extends CoreDBBase
{
    @Test
    public void connectTest() throws Exception
    {
        mConnection.startConnection();
        String huh = mConnection.read();
        Assert.assertEquals("Should be no such command text", "I didn't understand that.", huh);
    }
}
