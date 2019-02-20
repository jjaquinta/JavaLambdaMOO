package com.tsatsatzu.moo.core.data;

import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tsatsatzu.moo.core.data.val.MOOObjRef;

import jo.audio.util.IJSONAble;
import jo.audio.util.JSONUtils;
import jo.util.utils.obj.StringUtils;

public class MOOVerb implements IJSONAble
{
    public static final int DO_NONE = 0;
    public static final int DO_ANY = 1;
    public static final int DO_THIS = 2;
    
    public static final int IO_NONE = 0;
    public static final int IO_ANY = 1;
    public static final int IO_THIS = 2;
    
    public static final int PREP_NONE = 0;
    public static final int PREP_ANY = 1;           
    public static final int PREP_WITH = 2;          // with/using
    public static final int PREP_AT = 3;            // at/to
    public static final int PREP_IN_FRONT_OF = 4;   // in front of
    public static final int PREP_IN = 5;            // in/inside/into
    public static final int PREP_ON_TOP_OF = 6;     // on top of/on/onto/upon
    public static final int PREP_OUT_OF = 7;        // out of/from inside/from
    public static final int PREP_OVER = 8;          // over
    public static final int PREP_THROUGH = 9;       // through
    public static final int PREP_UNDER = 10;        // under/underneath/beneath
    public static final int PREP_BEHIND = 11;       // behind
    public static final int PREP_BESIDE = 12;       // beside
    public static final int PREP_FOR = 13;          // for/abount
    public static final int PREP_IS = 14;           // is
    public static final int PREP_AS = 15;           // as
    public static final int PREP_OFF = 16;          // off/off of
    
    private String      mName;
    private MOOObjRef   mOwner;
    private boolean     mRead;
    private boolean     mWrite;
    private boolean     mExecute;
    private int         mDirectObjectType;
    private int         mPrepositionType;
    private int         mIndirectObjectType;
    private List<String> mScript;

    // utilities

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", mName);
        json.put("owner", mOwner.getValue());
        json.put("read", mRead);
        json.put("write", mWrite);
        json.put("execute", mExecute);
        json.put("directObjectType", mDirectObjectType);
        json.put("prepositionType", mPrepositionType);
        json.put("indirectObjectType", mIndirectObjectType);
        JSONArray jscript = new JSONArray();
        json.put("script", jscript);
        for (String line: mScript)
            jscript.add(line);
        return json;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        mName = JSONUtils.getString(json, "name");
        mOwner = new MOOObjRef(JSONUtils.getInt(json, "owner"));
        mRead = JSONUtils.getBoolean(json, "read");
        mWrite = JSONUtils.getBoolean(json, "write");
        mExecute = JSONUtils.getBoolean(json, "execute");
        mDirectObjectType = JSONUtils.getInt(json, "directObjectType");
        mPrepositionType = JSONUtils.getInt(json, "prepositionType");
        mIndirectObjectType = JSONUtils.getInt(json, "indirectObjectType");
        JSONArray jscript = JSONUtils.getArray(json, "script");
        for (int i = 0; i < jscript.size(); i++)
        {
            String line = (String)jscript.get(i);
            mScript.add(line);
        }
    }

    public static String objTypeToStr(int type)
    {
        switch (type)
        {
            case DO_ANY:
                return "any";
            case DO_NONE:
                return "none";
            case DO_THIS:
                return "this";
        }
        throw new IllegalArgumentException("Unknown obj type="+type);
    }
    
    public static String prepTypeToStr(int type)
    {
        switch (type)
        {
            case -1: return "n/a";
            case PREP_NONE: return "none";
            case PREP_ANY: return "any";           
            case PREP_WITH: return "with/using";
            case PREP_AT: return "at/to";
            case PREP_IN_FRONT_OF: return "in front of";
            case PREP_IN: return "in/inside/into";
            case PREP_ON_TOP_OF: return "on top of/on/onto/upon";
            case PREP_OUT_OF: return "out of/from inside/from";
            case PREP_OVER: return "over";
            case PREP_THROUGH: return "through";
            case PREP_UNDER: return "under/underneath/beneath";
            case PREP_BEHIND: return "behind";
            case PREP_BESIDE: return "beside";
            case PREP_FOR: return "for/abount";
            case PREP_IS: return "is";
            case PREP_AS: return "as";
            case PREP_OFF: return "off/off of";
        }
        throw new IllegalArgumentException("Unknown prep type="+type);
    }
    
    public static int objStrToType(String str)
    {
        switch (str)
        {
            case "any": return DO_ANY;
            case "none": return DO_NONE;
            case "this": return DO_THIS;
        }
        throw new IllegalArgumentException("Unknown obj str="+str);
    }
    
    public static int prepStrToType(String str)
    {
        switch (str)
        {
            case "none": return PREP_NONE;
            case "any": return PREP_ANY;           
            case "with/using": return PREP_WITH;
            case "at/to": return PREP_AT;
            case "in front of": return PREP_IN_FRONT_OF;
            case "in/inside/into": return PREP_IN;
            case "on top of/on/onto/upon": return PREP_ON_TOP_OF;
            case "out of/from inside/from": return PREP_OUT_OF;
            case "over": return PREP_OVER;
            case "through": return PREP_THROUGH;
            case "under/underneath/beneath": return PREP_UNDER;
            case "behind": return PREP_BEHIND;
            case "beside": return PREP_BESIDE;
            case "for/abount": return PREP_FOR;
            case "is": return PREP_IS;
            case "as": return PREP_AS;
            case "off/off of": return PREP_OFF;
        }
        throw new IllegalArgumentException("Unknown prep str="+str);
    }

    public boolean isName(String verbName)
    {
        for (StringTokenizer st = new StringTokenizer(mName, " "); st.hasMoreTokens(); )
            if (isVerbName(st.nextToken(), verbName))
                return true;
        return false;
    }
    
    private boolean isVerbName(String ourName, String matchName)
    {
        int o = ourName.indexOf('*');
        if (o < 0)
            return ourName.equalsIgnoreCase(matchName);
        return matchName.startsWith(ourName.substring(0, o));
    }

    public String getScriptText()
    {
        return StringUtils.listize(mScript, "\n");
    }
    
    // getters and setters
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public MOOObjRef getOwner()
    {
        return mOwner;
    }
    public void setOwner(MOOObjRef owner)
    {
        mOwner = owner;
    }
    public boolean isRead()
    {
        return mRead;
    }
    public void setRead(boolean read)
    {
        mRead = read;
    }
    public boolean isWrite()
    {
        return mWrite;
    }
    public void setWrite(boolean write)
    {
        mWrite = write;
    }
    public boolean isExecute()
    {
        return mExecute;
    }
    public void setExecute(boolean exceute)
    {
        mExecute = exceute;
    }
    public int getDirectObjectType()
    {
        return mDirectObjectType;
    }
    public void setDirectObjectType(int directObjectType)
    {
        mDirectObjectType = directObjectType;
    }
    public int getPrepositionType()
    {
        return mPrepositionType;
    }
    public void setPrepositionType(int prepositionType)
    {
        mPrepositionType = prepositionType;
    }
    public int getIndirectObjectType()
    {
        return mIndirectObjectType;
    }
    public void setIndirectObjectType(int indirectObjectType)
    {
        mIndirectObjectType = indirectObjectType;
    }
    public List<String> getScript()
    {
        return mScript;
    }
    public void setScript(List<String> script)
    {
        mScript = script;
    }
}
