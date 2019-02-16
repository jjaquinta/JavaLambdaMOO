package com.tsatsatzu.moo.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.tsatsatzu.moo.test.api.ObjectAPITest;
import com.tsatsatzu.moo.test.api.PlayerAPITest;
import com.tsatsatzu.moo.test.api.PropertyAPITest;
import com.tsatsatzu.moo.test.api.VerbAPITest;

@RunWith(Suite.class)
@SuiteClasses({ ObjectAPITest.class, PlayerAPITest.class, PropertyAPITest.class,
        StandaloneScripts.class, VerbAPITest.class })
public class AllTests
{

}
