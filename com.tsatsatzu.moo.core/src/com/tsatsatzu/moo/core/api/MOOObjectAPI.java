package com.tsatsatzu.moo.core.api;

import java.util.HashSet;
import java.util.Set;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOPermissionException;
import com.tsatsatzu.moo.core.data.MOOProperty;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

public class MOOObjectAPI
{
    public static MOOProperty getProperty(MOOObject obj, String name) throws MOOException
    {
        while (obj != null)
        {
            MOOProperty prop = obj.getProperties().get(name);
            if (prop != null)
            {
                if (MOOObject.DEFAULT_PROPS.contains(name))
                    return prop;
                MOOObject programmer = MOOProgrammerLogic.getProgrammer();
                if (programmer.isWizard())
                    return prop;
                if (prop.isRead())
                    return prop;
                if (prop.getOwner().equals(programmer))
                    return prop;
                else
                    throw new MOOPermissionException("Programmer #"+programmer.getOID()+" does not have read permission on #"+obj.getOID());
            }
            obj = MOODbLogic.get(obj.getParent());
        }
        return null;
    }
    
    public static MOOValue getPropertyValue(MOOObject obj, String name) throws MOOException
    {
        MOOProperty prop = getProperty(obj, name);
        if (prop == null)
            return null;
        if (prop.isClear())
            return getPropertyValue(MOODbLogic.get(obj.getParent()), name);
        return prop.getValue();
    }
    
    public static void setPropertyValue(MOOObject obj, String name, MOOValue value) throws MOOException
    {
        MOOProperty prop = getProperty(obj, name);
        if (prop == null)
                throw new MOOException("No such value "+name+" on #"+obj.getOID());
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (programmer.isWizard())
            ;
        else if (prop.isWrite())
            ;
        else if (programmer.equals(prop.getOwner()))
            ;
        else
            throw new MOOPermissionException("Programmer #"+programmer.getOID()+" does not have write permission on #"+obj.getOID());
        prop.setValue(value);
        MOODbLogic.markDirty(obj.getOID());
    }
    
    /*
Creates and returns a new object whose parent is parent and whose owner is as described below. Either the given 
parent object must be #-1 or valid and fertile (i.e., its `f' bit must be set) or else the programmer must own 
parent or be a wizard; otherwise E_PERM is raised. E_PERM is also raised if owner is provided and not the same 
as the programmer, unless the programmer is a wizard. After the new object is created, its initialize verb, if 
any, is called with no arguments.

The new object is assigned the least non-negative object number that has not yet been used for a created object. 
Note that no object number is ever reused, even if the object with that number is recycled.

The owner of the new object is either the programmer (if owner is not provided), the new object itself (if owner 
was given as #-1), or owner (otherwise).

The other built-in properties of the new object are initialized as follows:

name         ""
location     #-1
contents     {}
programmer   0
wizard       0
r            0
w            0
f            0

The function `is_player()' returns false for newly created objects.

In addition, the new object inherits all of the other properties on parent. These properties have the same 
permission bits as on parent. If the `c' permissions bit is set, then the owner of the property on the new object 
is the same as the owner of the new object itself; otherwise, the owner of the property on the new object is the 
same as that on parent. The initial value of every inherited property is clear; see the description of the built-in 
function clear_property() for details.

If the intended owner of the new object has a property named `ownership_quota' and the value of that property is an 
integer, then create() treats that value as a quota. If the quota is less than or equal to zero, then the quota is 
considered to be exhausted and create() raises E_QUOTA instead of creating an object. Otherwise, the quota is 
decremented and stored back into the `ownership_quota' property as a part of the creation of the new object. 
     */
    public static MOOObjRef create(MOOObjRef parentRef, MOOObjRef owner) throws MOOException
    {
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        MOOObject parent = validateParent(programmer, parentRef);
        if ((owner != null) && !programmer.equals(owner) && !programmer.isWizard())
            throw new MOOPermissionException("Only wizard can set a different owner.");
        MOOObject created = MOODbLogic.newInstance((parent == null) ? -1 : parent.getOID());
        if (owner != null)
            if (owner.isNone())
                created.setOwner(created);
            else
                created.setOwner(owner);
        else
            created.setOwner(programmer);
        created.setName("");
        created.setLocation(-1);
        created.setContents(null);
        created.setProgrammer(false);
        created.setWizard(false);
        created.setRead(false);
        created.setWrite(false);
        created.setFertile(false);
        if (parent != null)
            for (MOOProperty prop : parent.getProperties().values())
                if (!created.getProperties().containsKey(prop.getName()))
                {
                    MOOProperty newProp = new MOOProperty(prop);
                    created.getProperties().put(newProp.getName(), newProp);
                }
        MOODbLogic.markDirty(created.getOID());
        MOOScriptLogic.executeScriptMaybe(programmer.toRef(), created, "initialize");
        return new MOOObjRef(created);
    }
    
    private static MOOObject validateParent(MOOObject programmer, MOOObjRef parentRef)  throws MOOException
    {
        if (parentRef == null)
            return null;
        MOOObject parent = MOODbLogic.get(parentRef.getValue());
        if (parent == null)
            throw new MOOPermissionException("Invalid parent #"+parentRef.getValue());
        if (programmer.isWizard())
            return parent;
        if (parent.isFertile())
            return parent;
        if (programmer.equals(parent.getOwner()))
            return parent;
        throw new MOOPermissionException("Invalid parent #"+parentRef.getValue());
    }
/*
Changes the parent of object to be new-parent. If object is not valid, or if new-parent is neither valid nor 
equal to #-1, then E_INVARG is raised. If the programmer is neither a wizard or the owner of object, or if 
new-parent is not fertile (i.e., its `f' bit is not set) and the programmer is neither the owner of new-parent 
nor a wizard, then E_PERM is raised. If new-parent is equal to object or one of its current ancestors, E_RECMOVE 
is raised. If object or one of its descendants defines a property with the same name as one defined either on 
new-parent or on one of its ancestors, then E_INVARG is raised.

Changing an object's parent can have the effect of removing some properties from and adding some other properties 
to that object and all of its descendants (i.e., its children and its children's children, etc.). Let common be 
the nearest ancestor that object and new-parent have in common before the parent of object is changed. Then all 
properties defined by ancestors of object under common (that is, those ancestors of object that are in turn 
descendants of common) are removed from object and all of its descendants. All properties defined by new-parent 
or its ancestors under common are added to object and all of its descendants. As with create(), the newly-added 
properties are given the same permission bits as they have on new-parent, the owner of each added property is 
either the owner of the object it's added to (if the `c' permissions bit is set) or the owner of that property 
on new-parent, and the value of each added property is clear; see the description of the built-in function 
clear_property() for details. All properties that are not removed or added in the reparenting process are completely 
unchanged.

If new-parent is equal to #-1, then object is given no parent at all; it becomes a new root of the 
parent/child hierarchy. In this case, all formerly inherited properties on object are simply removed. 
 */
    public static void chparent(MOOObjRef objRef, MOOObjRef newParent) throws MOOException
    {
        MOOObject object = MOODbLogic.get(objRef.getValue());
        if (object == null)
            throw new MOOException("Invalid object #"+objRef.getValue());
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        MOOObject parent = validateParent(programmer, newParent);
        // If new-parent is equal to object or one of its current ancestors, E_RECMOVE 
        // is raised.
        Set<String> oldProps = new HashSet<>();
        for (MOOObject op = object; op != null; op = MOODbLogic.get(op.getParent()))
            if (op.equals(newParent))
                throw new MOOException("New parent is an ancestor of the new object");
            else if (op != object)
                for (MOOProperty prop : op.getProperties().values())
                    if (prop.isDefinition())
                        oldProps.add(prop.getName());
        // If object or one of its descendants defines a property with the same name as one defined either on 
        // new-parent or on one of its ancestors, then E_INVARG is raised.
        Set<String> newProps = new HashSet<>();
        for (MOOObject op = parent; op != null; op = MOODbLogic.get(op.getParent()))
            for (MOOProperty prop : op.getProperties().values())
                if (prop.isDefinition())
                    newProps.add(prop.getName());
        validateDescendents(object, newProps);
        // remove old properties
        oldProps.removeAll(newProps);
        pruneProperties(object, oldProps);
        // switch parent
        object.setParent((parent == null) ? -1 : newParent.getValue());
        // propogate new properties
        if (parent != null)
        {
            parent.getChildren().add(object.getOID());
            inheritProperties(parent, object);
        }
    }
    
    private static void inheritProperties(MOOObject parent, MOOObject object)
    {
        for (MOOProperty prop : parent.getProperties().values())
            if (!object.getProperties().containsKey(prop.getName()))
                object.getProperties().put(prop.getName(), new MOOProperty(prop));
        MOODbLogic.markDirty(object.getOID());
        for (Integer childRef : object.getChildren())
        {
            MOOObject child = MOODbLogic.get(childRef);
            inheritProperties(object, child);
        }
    }

    private static void pruneProperties(MOOObject object, Set<String> newProps) throws MOOException
    {
        for (String name : newProps)
            object.getProperties().remove(name);
        for (Integer c : object.getChildren())
        {
            MOOObject child = MOODbLogic.get(c);
            pruneProperties(child, newProps);
        }
    }

    private static void validateDescendents(MOOObject object, Set<String> newProps) throws MOOException
    {
        for (MOOProperty prop : object.getProperties().values())
            if (prop.isDefinition() && newProps.contains(prop.getName()))
                throw new MOOException("descendent and new parent both declare '"+prop.getName()+"'");
        for (Integer c : object.getChildren())
        {
            MOOObject child = MOODbLogic.get(c);
            validateDescendents(child, newProps);
        }
    }
    
    // Returns a non-zero integer (i.e., a true value) if object is a valid object (one that has been created 
    // and not yet recycled) and zero (i.e., a false value) otherwise. 
    public static MOONumber valid(MOOObjRef ref)
    {
        return MOODbLogic.get(ref) != null ? MOONumber.TRUE : MOONumber.FALSE;
    }
    
    public static MOOObjRef parent(MOOObjRef ref) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(ref);
        if (obj == null)
            throw new MOOException("Invalid object '"+ref+"'");
        else
            return new MOOObjRef(obj.getParent());
    }
    
    public static MOOList children(MOOObjRef ref) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(ref);
        if (obj == null)
            throw new MOOException("Invalid object '"+ref+"'");
        MOOList list = new MOOList();
        for (Integer ch : obj.getChildren())
            list.add(new MOOObjRef(ch));
        return list;
    }
    
    public static void recycle(MOOObjRef ref)
    {
        MOODbLogic.recycle(ref.getValue());
    }
    
    public static MOONumber max_object()
    {
        return new MOONumber(MOODbLogic.getMaxObject());
    }
}
