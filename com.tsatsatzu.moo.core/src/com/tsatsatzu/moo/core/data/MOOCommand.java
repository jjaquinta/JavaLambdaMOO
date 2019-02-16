package com.tsatsatzu.moo.core.data;

import java.util.ArrayList;
import java.util.List;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

public class MOOCommand
{
    public static final int NONE = -1;
    public static final int NORMAL = 0;
    public static final int PROGRAM = 1;
    public static final int PREFIX = 2;
    public static final int SUFFIX = 3;
    public static final int FLUSH = 4;
    
    private int                    mType = NONE;
    private MOOObject              mPlayer;
    private String                 mArgstr;
    private List<String>           mArgs = new ArrayList<>();
    private String                 mVerbStr;
    private String                 mDObjStr;
    private MOOObjRef              mDObj;
    private String                 mPrepStr;
    private int                    mPrep;
    private String                 mIObjStr;
    private MOOObjRef              mIObj;
    private MOOObjRef              mCmdObj;
    private MOOVerb                mCmdVerb;
    
    public MOOObject getPlayer()
    {
        return mPlayer;
    }
    public void setPlayer(MOOObject player)
    {
        mPlayer = player;
    }
    public String getArgstr()
    {
        return mArgstr;
    }
    public void setArgstr(String argstr)
    {
        mArgstr = argstr;
    }
    public List<String> getArgs()
    {
        return mArgs;
    }
    public void setArgs(List<String> args)
    {
        mArgs = args;
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public String getVerbStr()
    {
        return mVerbStr;
    }
    public void setVerbStr(String verbStr)
    {
        mVerbStr = verbStr;
    }
    public String getDObjStr()
    {
        return mDObjStr;
    }
    public void setDObjStr(String dObjStr)
    {
        mDObjStr = dObjStr;
    }
    public MOOObjRef getDObj()
    {
        return mDObj;
    }
    public void setDObj(MOOObjRef dObj)
    {
        mDObj = dObj;
    }
    public String getPrepStr()
    {
        return mPrepStr;
    }
    public void setPrepStr(String prepStr)
    {
        mPrepStr = prepStr;
    }
    public int getPrep()
    {
        return mPrep;
    }
    public void setPrep(int prep)
    {
        mPrep = prep;
    }
    public String getIObjStr()
    {
        return mIObjStr;
    }
    public void setIObjStr(String iObjStr)
    {
        mIObjStr = iObjStr;
    }
    public MOOObjRef getIObj()
    {
        return mIObj;
    }
    public void setIObj(MOOObjRef iObj)
    {
        mIObj = iObj;
    }
    public MOOObjRef getCmdObj()
    {
        return mCmdObj;
    }
    public void setCmdObj(MOOObjRef cmdObj)
    {
        mCmdObj = cmdObj;
    }
    public MOOVerb getCmdVerb()
    {
        return mCmdVerb;
    }
    public void setCmdVerb(MOOVerb cmdVerb)
    {
        mCmdVerb = cmdVerb;
    }

}
