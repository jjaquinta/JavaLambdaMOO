package com.tsatsatzu.moo.core.data;

public class MOOStackElement
{
    private int mThis;
    private String mVerbName;
    private int mProgrammer;
    private int mVerbLoc;
    private int mPlayer;
    private int mLineNumber;
    public int getThis()
    {
        return mThis;
    }
    public void setThis(int this1)
    {
        mThis = this1;
    }
    public String getVerbName()
    {
        return mVerbName;
    }
    public void setVerbName(String verbName)
    {
        mVerbName = verbName;
    }
    public int getProgrammer()
    {
        return mProgrammer;
    }
    public void setProgrammer(int programmer)
    {
        mProgrammer = programmer;
    }
    public int getVerbLoc()
    {
        return mVerbLoc;
    }
    public void setVerbLoc(int verbLoc)
    {
        mVerbLoc = verbLoc;
    }
    public int getPlayer()
    {
        return mPlayer;
    }
    public void setPlayer(int player)
    {
        mPlayer = player;
    }
    public int getLineNumber()
    {
        return mLineNumber;
    }
    public void setLineNumber(int lineNumber)
    {
        mLineNumber = lineNumber;
    }

}
