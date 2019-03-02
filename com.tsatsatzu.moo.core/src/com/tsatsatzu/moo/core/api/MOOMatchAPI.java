package com.tsatsatzu.moo.core.api;

import java.util.List;
import java.util.StringTokenizer;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOMap;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;

import jo.ipa.logic.CMULogic;
import jo.ipa.logic.FuzzyMatchLogic;
import jo.ipa.logic.IPAComp;
import jo.ipa.logic.IPADictionary;
import jo.ipa.logic.IPAWord;
import jo.ipa.logic.LevenshteinDistance;

public class MOOMatchAPI
{
    public static MOOMap match_word(MOOString word, MOOString pattern) throws MOOException
    {
        IPAWord src = CMULogic.toIPA(word.getValue());
        IPAWord test = CMULogic.toIPA(pattern.getValue());
        IPAComp c = LevenshteinDistance.compare(src, test);
        MOOMap match = compToMap(c);
        return match;
    }
    
    public static MOOMap match_object(MOOString word, MOOObjRef pattern) throws MOOException
    {
        IPADictionary dict = new IPADictionary();
        addToDictionary(dict, null, pattern);
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(dict, word.getValue(), 1);
        if (matches.size() > 0)
            return compToMap(matches.get(0));
        else
            return null;
    }
    public static MOOMap match_verb(MOOString word, MOOObjRef pattern) throws MOOException
    {
        IPADictionary dict = new IPADictionary();
        addVerbsToDictionary(dict, null, pattern);
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(dict, word.getValue(), 1);
        if (matches.size() > 0)
            return compToMap(matches.get(0));
        else
            return null;
    }
    public static MOOList match_list(MOOString word, MOOList patterns, MOONumber limit) throws MOOException
    {
        int max = 1;
        if (limit != null)
            max = Math.max(max, ((MOONumber)limit).getValue().intValue());
        IPADictionary dict = new IPADictionary();
        addToDictionary(dict, null, patterns);
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(dict, word.getValue(), max);
        MOOList ret = new MOOList();
        for (IPAComp match : matches)
            ret.add(compToMap(match));
        return ret;
    }
    public static MOOList match_verbs(MOOString word, MOOList objects, MOONumber limit) throws MOOException
    {
        int max = 1;
        if (limit != null)
            max = Math.max(max, ((MOONumber)limit).getValue().intValue());
        IPADictionary dict = new IPADictionary();
        addVerbsToDictionary(dict, null, objects);
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(dict, word.getValue(), 1);
        MOOList ret = new MOOList();
        for (IPAComp match : matches)
            ret.add(compToMap(match));
        return ret;
    }

    private static void addToDictionary(IPADictionary dict, Object md, MOOValue value) throws MOOException
    {
        if (value instanceof MOOString)
            FuzzyMatchLogic.addToDictionary(dict, md, ((MOOString)value).getValue());
        else if (value instanceof MOOList)
        {
            MOOList l = (MOOList)value;
            for (int i = 0; i < l.size(); i++)
                addToDictionary(dict, md, l.get(i));
        }
        else if (value instanceof MOOObjRef)
        {
            MOOObject o = MOODbLogic.get((MOOObjRef)value);
            MOOValue name = MOOObjectAPI.getPropertyValue(o, "name");
            addToDictionary(dict, o, name);
            MOOValue alias = MOOObjectAPI.getPropertyValue(o, "aliases");
            addToDictionary(dict, o, alias);
        }
    }

    private static void addVerbsToDictionary(IPADictionary dict, Object md, MOOValue value) throws MOOException
    {
        if (value instanceof MOOObjRef)
        {
            MOOObject ori = MOODbLogic.get((MOOObjRef)value);
            for (MOOObject o = ori; o != null; o = MOODbLogic.get(o.getParent()))
                for (MOOVerb verb : o.getVerbs())
                {
                    if ((verb.getDirectObjectType() == MOOVerb.DO_THIS) && (verb.getPrepositionType() == MOOVerb.PREP_NONE) && (verb.getIndirectObjectType() == MOOVerb.IO_THIS))
                        continue; // function
                    String name = verb.getName();
                    for (StringTokenizer st = new StringTokenizer(name, " "); st.hasMoreTokens(); )
                    {
                        String vn = st.nextToken();
                        Object[] verbSpec = new Object[] { ori, vn };
                        addToDictionary(dict, verbSpec, new MOOString(vn));
                    }
                }
        }
        else if (value instanceof MOOList)
        {
            MOOList l = (MOOList)value;
            for (int i = 0; i < l.size(); i++)
                addVerbsToDictionary(dict, md, l.get(i));
        }
    }
    
    private static MOOMap compToMap(IPAComp c)
    {
        MOOMap match = new MOOMap();
        match.getValue().put("confidence", new MOONumber(c.getAdjustedDistance()));
        match.getValue().put("matchedWord", new MOOString(c.getWord2().getSource()));
        match.getValue().put("method", new MOONumber(c.getMethod()));
        match.getValue().put("originalWord", new MOOString(c.getWord1().getSource()));
        switch (c.getMethod())
        {
            case IPAComp.IPA:
                match.getValue().put("matchedIPA", new MOOString(c.getWord2().getIPA()));
                match.getValue().put("originalIPA", new MOOString(c.getWord1().getIPA()));
                break;
            case IPAComp.CAVERPHONE:
                match.getValue().put("matchedCaverphone", new MOOString(c.getWord2().getCaverphone()));
                match.getValue().put("originalCaverphone", new MOOString(c.getWord1().getCaverphone()));
                break;
            case IPAComp.DOUBLE_METAPHONE:
                match.getValue().put("matchedDoubleMetaphone", new MOOString(c.getWord2().getMetaphone()));
                match.getValue().put("originalDoubleMetaphone", new MOOString(c.getWord1().getMetaphone()));
                break;
        }
        if (c.getWord2().getMetadata() instanceof MOOObjRef)
            match.getValue().put("matchedObject", (MOOObjRef)c.getWord2().getMetadata());
        else if (c.getWord2().getMetadata() instanceof MOOObject)
            match.getValue().put("matchedObject", ((MOOObject)c.getWord2().getMetadata()).toRef());
        else if (c.getWord2().getMetadata() instanceof Object[])
        {
            Object[] verbSpec = (Object[])c.getWord2().getMetadata();
            match.getValue().put("matchedObject", ((MOOObject)verbSpec[0]).toRef());
            match.getValue().put("matchedVerb", new MOOString((String)verbSpec[1]));
        }
        return match;
    }
}
