package com.tsatsatzu.moo.core.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.data.MOOCommand;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class MOOParseLogic
{

    public static MOOCommand parse(MOOObject handler, MOOObjRef player, String inbuf) throws MOOException
    {
        try
        {
            MOOProgrammerLogic.pushProgrammer(player);
            MOOCommand cmd = new MOOCommand();
            cmd.setPlayer(MOODbLogic.get(player));
            cmd.setArgstr(inbuf);
            parseIntoWords(cmd);
            if (cmd.getArgs().size() == 0)
                return cmd;
            if (isSpecial(cmd))
                return cmd;
            if (handler.hasVerb("do_command"))
            {
                Map<String, Object> props = new HashMap<>();
                props.put("argstr", cmd.getArgstr());
                MOOValue val = MOOScriptLogic.executeScript(player, handler, "do_command", props, cmd.getArgs().toArray());
                if ((val instanceof MOONumber) && ((MOONumber)val).toBoolean())
                    return cmd;
            }
            parseIntoPartsOfSpeech(cmd);
            cmd.setDObj(findObject(cmd, cmd.getDObjStr()));
            cmd.setIObj(findObject(cmd, cmd.getIObjStr()));
            findCommand(cmd);
            if (cmd.getCmdVerb() != null)
                cmd.setType(MOOCommand.NORMAL);
            else
                cmd.setType(MOOCommand.NONE);
            return cmd;
        }
        finally
        {
            MOOProgrammerLogic.popProgrammer();
        }
    }
    
    private static void findCommand(MOOCommand cmd) throws MOOException
    {
        if (findCommandOnObject(cmd, cmd.getPlayer()))
            return;
        if (findCommandOnObject(cmd, MOODbLogic.get(cmd.getPlayer().getLocation())))
            return;
        if ((cmd.getDObj() != null) && !cmd.getDObj().isNone())
            if (findCommandOnObject(cmd, MOODbLogic.get(cmd.getDObj())))
                return;
        if ((cmd.getIObj() != null) && !cmd.getIObj().isNone())
            if (findCommandOnObject(cmd, MOODbLogic.get(cmd.getIObj())))
                return;
    }
    
    private static boolean findCommandOnObject(MOOCommand cmd, MOOObject obj)
    {
        MOOVerb v = obj.getVerb(cmd.getVerbStr());
        if (v == null)
            return false;
        // direct object
        if (v.getDirectObjectType() == MOOVerb.DO_NONE)
        {
            if ((cmd.getDObj() != null) && !cmd.getDObj().isNone())
                return false;
        }
        else if (v.getDirectObjectType() == MOOVerb.DO_ANY)
        {
            if ((cmd.getDObj() == null) || cmd.getDObj().isNone())
                return false;
        }
        else if (v.getDirectObjectType() == MOOVerb.DO_THIS)
        {
            if (!obj.equals(cmd.getDObj()))
                return false;
        }
        // preposition
        if (v.getPrepositionType() == MOOVerb.PREP_NONE)
        {
            if (cmd.getPrep() != MOOVerb.PREP_NONE)
                return false;
        }
        else if (v.getPrepositionType() == MOOVerb.PREP_ANY)
        {
            if (cmd.getPrep() == MOOVerb.PREP_NONE)
                return false;
        }
        else
        {
            if (cmd.getPrep() != v.getPrepositionType())
                return false;
        }
        // indirect object
        if (v.getIndirectObjectType() == MOOVerb.IO_NONE)
        {
            if ((cmd.getIObj() != null) && !cmd.getIObj().isNone())
                return false;
        }
        else if (v.getIndirectObjectType() == MOOVerb.IO_ANY)
        {
            if ((cmd.getIObj() == null) || cmd.getIObj().isNone())
                return false;
        }
        else if (v.getIndirectObjectType() == MOOVerb.IO_THIS)
        {
            if (!obj.equals(cmd.getIObj()))
                return false;
        }
        cmd.setCmdObj(obj.toRef());
        cmd.setCmdVerb(v);
        return true;
    }

    private static MOOObjRef findObject(MOOCommand cmd, String name) throws MOOException
    {
        if (StringUtils.isTrivial(name))
            return MOOObjRef.NONE;
        if (name.startsWith("#"))
            return new MOOObjRef(IntegerUtils.parseInt(name.substring(1)));
        MOOObjRef ref = indexObjectContents(cmd.getPlayer(), name);
        if (ref != null)
            return ref;
        MOOObject location = MOODbLogic.get(cmd.getPlayer().getLocation());
        if (location != null)
        {
            ref = indexObjectContents(location, name);
            if (ref != null)
                return ref;
            ref =indexObject(cmd.getPlayer(), "here");
            if (ref != null)
                return ref;
        }
        ref = indexObject(cmd.getPlayer(), "me");
        return ref;
    }
    
    private static boolean isSpecial(MOOCommand cmd)
    {
        switch (cmd.getArgs().get(0).toLowerCase())
        {
            case ".program":
                cmd.setType(MOOCommand.PROGRAM);
                return true;
            case "PREFIX":
            case "OUTPUTPREFIX":
                cmd.setType(MOOCommand.PREFIX);
                return true;
            case "SUFFIX":
            case "OUTPUTSUFFIX":
                cmd.setType(MOOCommand.SUFFIX);
                return true;
            case ".flush":
                cmd.setType(MOOCommand.FLUSH);
                return true;
        }
        return false;
    }
    
    private static PrepMatcher[] PREPS = new PrepMatcher[] {
            new PrepMatcher(MOOVerb.PREP_IN_FRONT_OF, "in", "front", "of"),
            new PrepMatcher(MOOVerb.PREP_ON_TOP_OF, "on", "top", "of"),
            new PrepMatcher(MOOVerb.PREP_OUT_OF, "from", "inside"),
            new PrepMatcher(MOOVerb.PREP_OUT_OF, "out", "of"),
            new PrepMatcher(MOOVerb.PREP_OFF, "off", "of"),
            new PrepMatcher(MOOVerb.PREP_WITH, "with"),
            new PrepMatcher(MOOVerb.PREP_WITH, "using"),
            new PrepMatcher(MOOVerb.PREP_AT, "at"),
            new PrepMatcher(MOOVerb.PREP_AT, "to"),
            new PrepMatcher(MOOVerb.PREP_IN, "in"),
            new PrepMatcher(MOOVerb.PREP_IN, "inside"),
            new PrepMatcher(MOOVerb.PREP_IN, "into"),
            new PrepMatcher(MOOVerb.PREP_ON_TOP_OF, "on"),
            new PrepMatcher(MOOVerb.PREP_ON_TOP_OF, "onto"),
            new PrepMatcher(MOOVerb.PREP_ON_TOP_OF, "upon"),
            new PrepMatcher(MOOVerb.PREP_OUT_OF, "from"),
            new PrepMatcher(MOOVerb.PREP_OVER, "over"),
            new PrepMatcher(MOOVerb.PREP_THROUGH, "through"),
            new PrepMatcher(MOOVerb.PREP_UNDER, "under"),
            new PrepMatcher(MOOVerb.PREP_UNDER, "underneath"),
            new PrepMatcher(MOOVerb.PREP_UNDER, "beneath"),
            new PrepMatcher(MOOVerb.PREP_BEHIND, "behind"),
            new PrepMatcher(MOOVerb.PREP_BESIDE, "beside"),
            new PrepMatcher(MOOVerb.PREP_FOR, "for"),
            new PrepMatcher(MOOVerb.PREP_FOR, "about"),
            new PrepMatcher(MOOVerb.PREP_IS, "is"),
            new PrepMatcher(MOOVerb.PREP_AS, "as"),
            new PrepMatcher(MOOVerb.PREP_OFF, "off")
    };
    
    private static void parseIntoPartsOfSpeech(MOOCommand cmd)
    {
        cmd.setVerbStr(cmd.getArgs().get(0));
        int prep = MOOVerb.PREP_NONE;
        int prepStart = -1;
        int prepEnd = -1;
        for (int i = 1; i < cmd.getArgs().size(); i++)
        {
            for (PrepMatcher m : PREPS)
                if (m.isMatch(cmd.getArgs(), i))
                {
                    prep = m.getPrep();
                    prepStart = i;
                    prepEnd = i + m.getWords().length;
                    break;
                }
            if (prep != MOOVerb.PREP_NONE)
                break;
        }
        if (prep == MOOVerb.PREP_NONE)
        {
            if (cmd.getArgs().size() > 1)
                cmd.setDObjStr(join(cmd.getArgs(), 1, -1));
            cmd.setPrep(prep);
            cmd.setIObjStr(null);
            return;
        }
        cmd.setDObjStr(join(cmd.getArgs(), 1, prepStart));
        cmd.setPrep(prep);
        cmd.setPrepStr(join(cmd.getArgs(), prepStart, prepEnd));
        if (prepEnd < cmd.getArgs().size())
            cmd.setIObjStr(join(cmd.getArgs(), prepEnd, -1));
        else
            cmd.setIObjStr(null);
    }
    
    private static String join(List<String> args, int start, int end)
    {
        if (end < 0)
            end = args.size();
        StringBuffer sb = new StringBuffer();
        for (int i = start; i < end; i++)
        {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(args.get(i));
        }
        return sb.toString();
    }

    private static void parseIntoWords(MOOCommand cmd)
    {
        String inbuf = cmd.getArgstr().trim();
        if (inbuf.startsWith("\""))
            inbuf = "say "+inbuf.substring(1).trim();
        else if (inbuf.startsWith(":"))
            inbuf = "emote "+inbuf.substring(1).trim();
        else if (inbuf.startsWith(";"))
            inbuf = "eval "+inbuf.substring(1).trim();
        StringBuffer word = new StringBuffer();
        boolean inQuote = false;
        boolean inEscape = false;
        for (char ch : inbuf.toCharArray())
            if (inEscape)
            {
                word.append(ch);
                inEscape = false;
            }
            else if (ch == ' ')
            {
                if (inQuote)
                    word.append(ch);
                else if (word.length() > 0)
                {
                    cmd.getArgs().add(word.toString());
                    word.setLength(0);
                }
            }
            else if (ch == '\\')
            {
                inEscape = true;
            }
            else if (ch == '\"')
            {
                if (inQuote)
                    inQuote = false;
                else 
                    inQuote = true;
            }
            else
                word.append(ch);
        if (word.length() > 0)
            cmd.getArgs().add(word.toString());
    }

    public static MOOObjRef indexObjectContents(MOOObject container, String name)
            throws MOOException
    {
        for (MOOValue onum : container.getContents())
        {
            int oid = ((MOONumber)onum).getValue().intValue();
            MOOObject obj = MOODbLogic.get(oid);
            MOOObjRef ref = indexObject(obj, name);
            if (ref != null)
                return ref;
        }
        return null;
    }

    public static MOOObjRef indexObject(MOOObject obj, String name)
            throws MOOException
    {
        MOOObjRef ref = indexObject(obj, obj.getName(), name);
        if (ref != null)
            return ref;
        MOOValue val = MOOObjectAPI.getPropertyValue(obj, "aliases");
        if (val instanceof MOOString)
        {
            ref = indexObject(obj, ((MOOString)val).getValue(), name);
            if (ref != null)
                return ref;
        }
        else if (val instanceof MOOList)
            for (MOOValue v2 : ((MOOList)val).getValue())
                if (v2 instanceof MOOString)
                {
                    ref = indexObject(obj, ((MOOString)v2).getValue(), name);
                    if (ref != null)
                        return ref;
                }
        return null;
    }

    private static MOOObjRef indexObject(MOOObject obj, String objname, String name)
    {
        for (StringTokenizer st = new StringTokenizer(objname, " "); st.hasMoreTokens(); )
            if(st.nextToken().equals(name))
                return obj.toRef();
        return null;
    }
}

class PrepMatcher
{
    private String[]    mWords;
    private int         mPrep;
    
    public PrepMatcher(int prep, String... words)
    {
        mPrep = prep;
        mWords = words;
    }
    
    public boolean isMatch(List<String> args, int idx)
    {
        if (idx + mWords.length > args.size())
            return false;
        for (int i = 0; i < mWords.length; i++)
            if (!args.get(idx + i).equals(mWords[i]))
                return false;
        return true;
    }

    public int getPrep()
    {
        return mPrep;
    }

    public String[] getWords()
    {
        return mWords;
    }
}