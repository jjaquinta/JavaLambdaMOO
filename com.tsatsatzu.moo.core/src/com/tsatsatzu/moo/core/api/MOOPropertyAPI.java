package com.tsatsatzu.moo.core.api;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOProperty;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MOOPropertyAPI
{
    /*
     * Returns a list of the names of the properties defined directly on the given object, not inherited from its parent. 
     * If object is not valid, then E_INVARG is raised. If the programmer does not have read permission on object, 
     * then E_PERM is raised. 
     */
    public static MOOList properties(MOOObjRef objref) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objref);
        if (obj == null)
            throw new MOOException("Invalid arg="+objref);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !obj.isRead() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on="+objref);
        MOOList list = new MOOList();
        for (MOOProperty prop : obj.getProperties().values())
            if (prop.isDefinition())
                list.getValue().add(new MOOString(prop.getName()));
        return list;
    }
    
    /*
     These two functions get and set (respectively) the owner and permission bits for the property named prop-name on the given object. 
     If object is not valid, then E_INVARG is raised. If object has no non-built-in property named prop-name, then E_PROPNF is raised. 
     If the programmer does not have read (write) permission on the property in question, then property_info() (set_property_info()) 
     raises E_PERM. 
     Property info has the following form:
        {owner, perms [, new-name]}
     where owner is an object, perms is a string containing only characters from the set `r', `w', and `c', and new-name is a string; 
     new-name is never part of the value returned by property_info(), but it may optionally be given as part of the value provided 
     to set_property_info(). This list is the kind of value returned by property_info() and expected as the third argument to set_property_info(); 
     the latter function raises E_INVARG if owner is not valid, if perms contains any illegal characters, or, when new-name is given, 
     if prop-name is not defined directly on object or new-name names an existing property defined on object or any of its ancestors or descendants. 
     */
    
    public static MOOList property_info(MOOObjRef objref, MOOString propName) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objref);
        if (obj == null)
            throw new MOOException("Invalid arg="+objref);
        MOOProperty prop = obj.getProperties().get(propName.getValue());
        if (prop == null)
            throw new MOOException("Invalid prop="+propName+" on obj="+objref);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !prop.isRead() && !prop.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission prop="+propName+" on obj="+objref);
        MOOList info = new MOOList();
        info.getValue().add(prop.getOwner());
        String perms = "";
        if (prop.isRead())
            perms += "r";
        if (prop.isWrite())
            perms += "w";
        if (prop.isChange())
            perms += "c";
        info.getValue().add(new MOOString(perms));
        return info;
    }
    
    public static void set_property_info(MOOObjRef objref, MOOString propName, MOOList info) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objref);
        if (obj == null)
            throw new MOOException("Invalid arg="+objref);
        MOOProperty prop = obj.getProperties().get(propName.getValue());
        if (prop == null)
            throw new MOOException("Invalid prop="+propName.getValue()+" on obj="+objref);
        if (!prop.isDefinition())
            throw new MOOException("Property prop="+propName.getValue()+" on obj="+objref+" must be defined directly on object");
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !prop.isWrite() && !prop.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission prop="+propName+" on obj="+objref);
        MOOObject newOwner = null;
        if (info.getValue().size() > 0)
        {
            if (!(info.getValue().get(0) instanceof MOOObjRef))
                throw new MOOException("Expected object value for first element in info, not "+info.getValue().get(0));
            MOOObjRef newOwnerRef = (MOOObjRef)info.getValue().get(0);
            newOwner = MOODbLogic.get(newOwnerRef);
            if (newOwner == null)
                throw new MOOException("Object value for new owner element in info invalid "+info.getValue().get(0));
        }
        boolean read = false;
        boolean write = false;
        boolean change = false;
        if (info.getValue().size() > 1)
        {
            if (!(info.getValue().get(1) instanceof MOOString))
                throw new MOOException("Expected string value for second element in info, not "+info.getValue().get(1));
            MOOString propPerms = (MOOString)info.getValue().get(1);
            for (char ch : propPerms.getValue().toCharArray())
                if (ch == 'r')
                    read = true;
                else if (ch == 'w')
                    write = true;
                else if (ch == 'c')
                    change = true;
                else
                    throw new MOOException("Unexpected values '"+ch+"' in permission string '"+propPerms+"'");
        }
        String newName = null;
        if (info.getValue().size() > 2)
        {
            if (!(info.getValue().get(2) instanceof MOOString))
                throw new MOOException("Expected string value for third element in info, not "+info.getValue().get(2));
            newName = ((MOOString)info.getValue().get(2)).getValue();
            if (doesPropExist(obj, newName))
                throw new MOOException("New property name '"+newName+"' exists on object or descendent");
        }
        prop.setOwner(new MOOObjRef(newOwner));
        prop.setRead(read);
        prop.setWrite(write);
        prop.setChange(change);
        if (newName != null)
            changeName(obj, prop.getName(), newName);
    }
    
    private static void changeName(MOOObject obj, String oldName, String newName)
    {
        MOOProperty prop = obj.getProperties().get(oldName);
        obj.getProperties().remove(oldName);
        prop.setName(newName);
        obj.getProperties().put(newName, prop);
        MOODbLogic.markDirty(obj.getOID());
        for (int ch : obj.getChildren())
        {
            MOOObject child = MOODbLogic.get(ch);
            changeName(child, oldName, newName);
        }
    }
    
    private static boolean doesPropExist(MOOObject obj, String propName)
    {
        if (obj.getProperties().containsKey(propName))
            if (obj.getProperties().get(propName).isDefinition())
                return true;
        for (int ch : obj.getChildren())
        {
            MOOObject child = MOODbLogic.get(ch);
            if (doesPropExist(child, propName))
                return true;
        }
        return false;
    }
    
    /*
     Defines a new property on the given object, inherited by all of its descendants; the property is named prop-name, 
     its initial value is value, and its owner and initial permission bits are given by info in the same format as 
     is returned by property_info(), described above. If object is not valid or info does not specify a valid owner 
     and well-formed permission bits or object or its ancestors or descendants already defines a property named prop-name, 
     then E_INVARG is raised. If the programmer does not have write permission on object or if the owner specified by 
     info is not the programmer and the programmer is not a wizard, then E_PERM is raised. 
     */
    
    public static void add_property(MOOObjRef objRef, MOOString propName, MOOValue value, MOOList info) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOObject newOwner = null;
        if (info.getValue().size() == 0)
            throw new MOOException("Expected object value for first element in info, none present");
        if (!(info.getValue().get(0) instanceof MOOObjRef))
            throw new MOOException("Expected object value for first element in info, not "+info.getValue().get(0));
        MOOObjRef newOwnerRef = (MOOObjRef)info.getValue().get(0);
        newOwner = MOODbLogic.get(newOwnerRef);
        if (newOwner == null)
            throw new MOOException("Object value for new owner element in info invalid "+info.getValue().get(0));
        boolean read = false;
        boolean write = false;
        boolean change = false;
        if (info.getValue().size() == 1)
            throw new MOOException("Expected string value for second element in info, none present");
        if (!(info.getValue().get(1) instanceof MOOString))
            throw new MOOException("Expected string value for second element in info, not "+info.getValue().get(1));
        MOOString propPerms = (MOOString)info.getValue().get(1);
        for (char ch : propPerms.getValue().toCharArray())
            if (ch == 'r')
                read = true;
            else if (ch == 'w')
                write = true;
            else if (ch == 'c')
                change = true;
            else
                throw new MOOException("Unexpected values '"+ch+"' in permission string '"+propPerms+"'");
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !obj.isWrite() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        if (doesPropExist(obj, propName.getValue()))
            throw new MOOException(propName+" exists on descendent of obj="+objRef);
        for (MOOObject o = obj; o != null; o = MOODbLogic.get(o.getParent()))
            if (o.getProperties().containsKey(propName) && o.getProperties().get(propName).isDefinition())
                throw new MOOException(propName+" exists on ancestor of obj="+objRef);
        MOOProperty prop = new MOOProperty();
        prop.setDefinition(true);
        prop.setName(propName.getValue());
        prop.setOwner(newOwnerRef);
        prop.setRead(read);
        prop.setWrite(write);
        prop.setChange(change);
        prop.setValue(value);
        obj.getProperties().put(prop.getName(), prop);
        MOODbLogic.markDirty(obj.getOID());
        addToChildren(obj, prop);
    }
    
    private static void addToChildren(MOOObject obj, MOOProperty base)
    {
        for (Integer c : obj.getChildren())
        {
            MOOObject child = MOODbLogic.get(c);
            child.getProperties().put(base.getName(), new MOOProperty(base));
            MOODbLogic.markDirty(child.getOID());
            addToChildren(child, base);
        }
        
    }

    /*
     Removes the property named prop-name from the given object and all of its descendants. 
     If object is not valid, then E_INVARG is raised. If the programmer does not have write permission on object, 
     then E_PERM is raised. If object does not directly define a property named prop-name (as opposed to inheriting 
     one from its parent), then E_PROPNF is raised. 
     */
    
    public static void delete_property(MOOObjRef objRef, MOOString propName) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        if (!obj.getProperties().containsKey(propName.getValue()) || !obj.getProperties().get(propName.getValue()).isDefinition())
            throw new MOOException("Property "+propName.getValue()+" is not defined on "+objRef);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !obj.isWrite() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        removeProperty(obj, propName.getValue());
    }
    
    private static void removeProperty(MOOObject obj, String value)
    {
        obj.getProperties().remove(value);
        MOODbLogic.markDirty(obj.getOID());
        for (Integer c : obj.getChildren())
        {
            MOOObject child = MOODbLogic.get(c);
            removeProperty(child, value);
        }
    }

    /*
        These two functions test for clear and set to clear, respectively, 
        the property named prop-name on the given object. If object is not valid, 
        then E_INVARG is raised. If object has no non-built-in property named prop-name, 
        then E_PROPNF is raised. If the programmer does not have read (write) permission 
        on the property in question, then is_clear_property() (clear_property()) 
        raises E_PERM. If a property is clear, then when the value of that property is 
        queried the value of the parent's property of the same name is returned. 
        If the parent's property is clear, then the parent's parent's value is examined, and so on. 
        If object is the definer of the property prop-name, as opposed to an inheritor of the property, 
        then clear_property() raises E_INVARG. 
     */
    public static MOONumber is_clear_property(MOOObjRef objRef, MOOString propName) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        if (!obj.getProperties().containsKey(propName.getValue()))
            throw new MOOException("Property "+propName.getValue()+" is not defined on "+objRef);
        MOOProperty prop = obj.getProperties().get(propName.getValue());
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !prop.isRead() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on obj="+objRef);
        return prop.isClear() ? MOONumber.TRUE : MOONumber.FALSE;
    }
    
    public static void clear_property(MOOObjRef objRef, MOOString propName) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        if (!obj.getProperties().containsKey(propName.getValue()))
            throw new MOOException("Property "+propName.getValue()+" is not defined on "+objRef);
        MOOProperty prop = obj.getProperties().get(propName.getValue());
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !prop.isWrite() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on obj="+objRef);
        if (prop.isClear())
            return;
        if (prop.isDefinition())
            throw new MOOException(propName.getValue()+" is defined on obj="+objRef+", and cannot be cleared");
        prop.setClear(true);
        MOODbLogic.markDirty(objRef);
    }

    public static MOOValue get_property(MOOObjRef objRef, MOOString propName) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        if (!obj.getProperties().containsKey(propName.getValue()))
            throw new MOOException("Property "+propName.getValue()+" is not defined on "+objRef);
        MOOProperty prop = obj.getProperties().get(propName.getValue());
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !prop.isRead() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on obj="+objRef);
        if (prop.isClear())
            return get_property(new MOOObjRef(obj.getParent()), propName);
        else
            return prop.getValue();
    }
    
    public static void set_property(MOOObjRef objRef, MOOString propName, MOOValue val) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        if (!obj.getProperties().containsKey(propName.getValue()))
            throw new MOOException("Property "+propName.getValue()+" is not defined on "+objRef);
        MOOProperty prop = obj.getProperties().get(propName.getValue());
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !prop.isWrite() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        prop.setValue(val);
        prop.setClear(false);
    }
}
