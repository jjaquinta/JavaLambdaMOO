package com.tsatsatzu.moo.test.util.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOProperty;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;

public class DumpCoreVerbs
{
    private BufferedWriter  mWtr;
    
    private void setup() throws Exception
    {
        System.setProperty("moo.store.class", "com.tsatsatzu.moo.test.util.CoreJDBStore");
        MOOOpsLogic.initailize();
        mWtr = new BufferedWriter(new FileWriter(new File("C:\\Users\\JoGrant\\git\\JavaLambdaMOO\\com.tsatsatzu.moo.core\\dbs\\LambdaCore-20Jun19.txt")));
    }
    
    private void setdown() throws Exception
    {
        mWtr.close();
        MOOOpsLogic.shutdown();
    }
    
    private void println(String str) throws IOException
    {
        mWtr.write(str);
        mWtr.newLine();
    }
    
    private void dumpVerbs() throws Exception
    {
        int max = MOODbLogic.getMaxObject();
        for (int i = 0; i <= max; i++)
        {
            MOOObject obj = MOODbLogic.get(i);
            if (obj == null)
                continue;
            println("================================#"+obj.getOID()+" "+obj.getName()+"===============================================");
            for (MOOProperty prop : obj.getProperties().values())
                if (prop.isDefinition())
                {
                    String perms = "";
                    if (prop.isRead())
                        perms += "r";
                    if (prop.isWrite())
                        perms += "w";
                    if (prop.isChange())
                        perms += "c";
                    println("#"+obj.getOID()+" "+obj.getName()+"."+prop.getName()+" "+perms+" = "+prop.getValue().getClass().getSimpleName()+" "+prop.getValue());
                }
            for (MOOVerb verb : obj.getVerbs())
            {
                println("#"+obj.getOID()+" "+obj.getName()+":"+verb.getName()
                        +" "+MOOVerb.objTypeToStr(verb.getDirectObjectType())
                        +" "+MOOVerb.prepTypeToStr(verb.getPrepositionType())
                        +" "+MOOVerb.objTypeToStr(verb.getIndirectObjectType()));
                if (verb.getScript() == null)
                    continue;
                for (String str : verb.getScript())
                    println(str);
                println("-------------------------------------------------------------------------------");
            }
        }
    }
    
    public void run()
    {
        try
        {
            setup();
            dumpVerbs();
            setdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv)
    {
        DumpCoreVerbs app = new DumpCoreVerbs();
        app.run();
    }
}
