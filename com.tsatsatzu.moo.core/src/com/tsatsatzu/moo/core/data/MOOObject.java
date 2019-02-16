package com.tsatsatzu.moo.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tsatsatzu.moo.core.api.MOOObjectAPI;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOOMap;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;

public class MOOObject
{
    public static final String PROP_NAME = "name";
    public static final String PROP_OWNER = "owner";
    public static final String PROP_LOCATION = "location";
    public static final String PROP_CONTENTS = "contents";
    public static final String PROP_PROGRAMMER = "programmer";
    public static final String PROP_WIZARD = "wizard";
    public static final String PROP_READ = "r";
    public static final String PROP_WRITE = "w";
    public static final String PROP_FERTILE = "f";
    
    public static Set<String> DEFAULT_PROPS = new HashSet<>();
    static
    {
        DEFAULT_PROPS.add(PROP_NAME);
        DEFAULT_PROPS.add(PROP_OWNER);
        DEFAULT_PROPS.add(PROP_LOCATION);
        DEFAULT_PROPS.add(PROP_CONTENTS);
        DEFAULT_PROPS.add(PROP_PROGRAMMER);
        DEFAULT_PROPS.add(PROP_WIZARD);
        DEFAULT_PROPS.add(PROP_READ);
        DEFAULT_PROPS.add(PROP_WRITE);
        DEFAULT_PROPS.add(PROP_FERTILE);  
    }
    
    private int                      mOID;
    private Integer                  mParent;
    private List<Integer>            mChildren   = new ArrayList<>();
    private boolean                  mPlayer;
    private Map<String, MOOProperty> mProperties = new HashMap<>();
    private List<MOOVerb>            mVerbs = new ArrayList<>();

    public MOOObject()
    {
        for (String name : DEFAULT_PROPS)
        {
            MOOProperty prop = new MOOProperty();
            prop.setName(name);
            prop.setRead(true);
            prop.setWrite(true);
            prop.setChange(true);
            switch (name)
            {
                case PROP_NAME:
                    prop.setValue(new MOOString());
                    break;
                case PROP_OWNER:
                    prop.setValue(new MOOObjRef());
                    break;
                case PROP_LOCATION:
                    prop.setValue(new MOOObjRef());
                    break;
                case PROP_CONTENTS:
                    prop.setValue(new MOOList());
                    break;
                case PROP_PROGRAMMER:
                    prop.setValue(MOONumber.FALSE);
                    break;
                case PROP_WIZARD:
                    prop.setValue(MOONumber.FALSE);
                    break;
                case PROP_READ:
                    prop.setValue(MOONumber.TRUE);
                    break;
                case PROP_WRITE:
                    prop.setValue(MOONumber.TRUE);
                    break;
                case PROP_FERTILE:
                    prop.setValue(MOONumber.FALSE);
                    break;
            }
            mProperties.put(name, prop);            
        }
    }
    
    // utilities
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MOOObjRef)
            return ((MOOObjRef)obj).getValue() == mOID;
        else if (obj instanceof MOOObject)
            return ((MOOObject)obj).getOID() == mOID;
        else if (obj instanceof Number)
            return ((Number)obj).intValue() == mOID;
        else if (obj == null)
            return -1 == mOID;
        return false;
    }

    public MOOObjRef toRef()
    {
        return new MOOObjRef(mOID);
    }
    
    public MOOVerb getVerb(String verbName)
    {
        for (MOOVerb verb : mVerbs)
            if (verb.isName(verbName))
                return verb;
        if (mParent != -1)
            return MOODbLogic.get(mParent).getVerb(verbName);
        return null;
    }

    public boolean hasVerb(String verbName)
    {
        return getVerb(verbName) != null;
    }

    public boolean getBoolean(String propName) throws MOOException
    {
        MOOProperty prop = MOOObjectAPI.getProperty(this, propName);
        if (prop == null)
            return false;
        return prop.getValue().toBoolean();
    }

    public String getString(String propName) throws MOOException
    {
        MOOProperty prop = MOOObjectAPI.getProperty(this, propName);
        if (prop == null)
            return null;
        return prop.getValue().toStringVal().getValue();
    }

    public List<MOOValue> getList(String propName) throws MOOException
    {
        MOOProperty prop = MOOObjectAPI.getProperty(this, propName);
        if (prop == null)
            return null;
        return prop.getValue().toList().getValue();
    }
    
    public Map<String,MOOValue> getMap(String propName) throws MOOException
    {
        MOOProperty prop = MOOObjectAPI.getProperty(this, propName);
        if (prop == null)
            return null;
        return prop.getValue().toMap().getValue();
    }
    
    public Number getNumber(String propName) throws MOOException
    {
        MOOProperty prop = MOOObjectAPI.getProperty(this, propName);
        if (prop == null)
            return null;
        return prop.getValue().toNumber().getValue();
    }
    
    public MOOObjRef getObjRef(String propName) throws MOOException
    {
        MOOProperty prop = MOOObjectAPI.getProperty(this, propName);
        if (prop == null)
            return null;
        return prop.getValue().toObjRef();
    }

    public void setBoolean(String propName, boolean val) throws MOOException
    {
        MOOObjectAPI.setPropertyValue(this, propName, new MOONumber(val ? 1 : 0));
    }

    public void setString(String propName, String val) throws MOOException
    {
        MOOObjectAPI.setPropertyValue(this, propName, new MOOString(val));
    }

    public void setList(String propName, List<MOOValue> val) throws MOOException
    {
        if ((val != null) && (val.size() > 0))
            throw new IllegalStateException("Not supported yet setting non-empty lists");
        MOOObjectAPI.setPropertyValue(this, propName, new MOOList());
    }
    
    public void setMap(String propName, Map<String,MOOValue> val) throws MOOException
    {
        if ((val != null) && (val.size() > 0))
            throw new IllegalStateException("Not supported yet setting non-empty map");
        MOOObjectAPI.setPropertyValue(this, propName, new MOOMap());
    }
    
    public void setNumber(String propName, Number val) throws MOOException
    {
        MOOObjectAPI.setPropertyValue(this, propName, new MOONumber(val.intValue()));
    }
    
    public void setObjRef(String propName, int val) throws MOOException
    {
        MOOObjectAPI.setPropertyValue(this, propName, new MOOObjRef(val));
    }
    
    public void setObjRef(String propName, MOOObjRef val) throws MOOException
    {
        setObjRef(propName, val.getValue());
    }
    
    public void setObjRef(String propName, MOOObject val) throws MOOException
    {
        setObjRef(propName, val.getOID());
    }
    
    // pseudo getters and setters
    public String getName() throws MOOException
    {
        return getString(PROP_NAME);
    }
    
    public MOOObjRef getOwner() throws MOOException
    {
        return getObjRef(PROP_OWNER);
    }
    
    public MOOObjRef getLocation() throws MOOException
    {
        return getObjRef(PROP_LOCATION);
    }
    
    public List<MOOValue> getContents() throws MOOException
    {
        return getList(PROP_CONTENTS);
    }
    
    public boolean isWizard() throws MOOException
    {
        return getBoolean(PROP_WIZARD);
    }
    
    public boolean isFertile() throws MOOException
    {
        return getBoolean(PROP_FERTILE);
    }
    
    public boolean isRead() throws MOOException
    {
        return getBoolean(PROP_READ);
    }
    
    public boolean isWrite() throws MOOException
    {
        return getBoolean(PROP_WRITE);
    }
    
    public boolean isProgrammer() throws MOOException
    {
        return getBoolean(PROP_PROGRAMMER);
    }
    
    public void setName(String val) throws MOOException
    {
        setString(PROP_NAME, val);
    }
    
    public void setOwner(MOOObjRef val) throws MOOException
    {
        setObjRef(PROP_OWNER, val);
    }
    
    public void setOwner(MOOObject val) throws MOOException
    {
        setObjRef(PROP_OWNER, val);
    }
    
    public void setLocation(MOOObjRef val) throws MOOException
    {
        setObjRef(PROP_LOCATION, val);
    }
    
    public void setLocation(int val) throws MOOException
    {
        setObjRef(PROP_LOCATION, val);
    }
    
    public void setContents(List<MOOValue> val) throws MOOException
    {
        setList(PROP_CONTENTS, val);
    }
    
    public void setWizard(boolean val) throws MOOException
    {
        setBoolean(PROP_WIZARD, val);
    }
    
    public void setFertile(boolean val) throws MOOException
    {
        setBoolean(PROP_FERTILE, val);
    }
    
    public void setRead(boolean val) throws MOOException
    {
        setBoolean(PROP_READ, val);
    }
    
    public void setWrite(boolean val) throws MOOException
    {
        setBoolean(PROP_WRITE, val);
    }
    
    public void setProgrammer(boolean val) throws MOOException
    {
        setBoolean(PROP_PROGRAMMER, val);
    }
    
    // getters and setters
    
    public int getOID()
    {
        return mOID;
    }

    public void setOID(int oID)
    {
        mOID = oID;
    }

    public Integer getParent()
    {
        return mParent;
    }

    public void setParent(Integer parent)
    {
        mParent = parent;
    }

    public List<Integer> getChildren()
    {
        return mChildren;
    }

    public void setChildren(List<Integer> children)
    {
        mChildren = children;
    }

    public boolean isPlayer()
    {
        return mPlayer;
    }

    public void setPlayer(boolean player)
    {
        mPlayer = player;
    }

    public Map<String, MOOProperty> getProperties()
    {
        return mProperties;
    }

    public void setProperties(Map<String, MOOProperty> properties)
    {
        mProperties = properties;
    }

    public List<MOOVerb> getVerbs()
    {
        return mVerbs;
    }

    public void setVerbs(List<MOOVerb> verbs)
    {
        mVerbs = verbs;
    }
}
