package com.tsatsatzu.moo.core.api;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;
import com.tsatsatzu.moo.core.logic.script.MOOScriptLogic;

public class MOOMoveAPI
{
    /*
    Changes what's location to be where. This is a complex process because a number of permissions 
    checks and notifications must be performed. The actual movement takes place as described in the 
    following paragraphs.

    what should be a valid object and where should be either a valid object or #-1 (denoting a location 
    of `nowhere'); otherwise E_INVARG is raised. The programmer must be either the owner of what or a 
    wizard; otherwise, E_PERM is raised.

    If where is a valid object, then the verb-call

    where:accept(what)

    is performed before any movement takes place. If the verb returns a false value and the programmer 
    is not a wizard, then where is considered to have refused entrance to what; move() raises E_NACC. 
    If where does not define an accept verb, then it is treated as if it defined one that always returned 
    false.

    If moving what into where would create a loop in the containment hierarchy (i.e., what would contain 
    itself, even indirectly), then E_RECMOVE is raised instead.

    The `location' property of what is changed to be where, and the `contents' properties of the old and 
    new locations are modified appropriately. Let old-where be the location of what before it was moved. 
    If old-where is a valid object, then the verb-call

    old-where:exitfunc(what)

    is performed and its result is ignored; it is not an error if old-where does not define a verb named 
    `exitfunc'. Finally, if where and what are still valid objects, and where is still the location of 
    what, then the verb-call

    where:enterfunc(what)

    is performed and its result is ignored; again, it is not an error if where does not define a verb 
    named `enterfunc'. 
     */
    public static void move(MOOObjRef whatRef, MOOObjRef whereRef) throws MOOException
    {
        MOOObject what = MOODbLogic.get(whatRef);
        if (what == null)
            throw new MOOException("Invalid what="+whatRef);
        MOOObject where = MOODbLogic.get(whereRef);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !what.getOwner().equals(programmer))
            throw new MOOException("You are not allowed to move ="+whatRef);
    
        if (where.hasVerb("accept"))
        {
            MOOValue val = MOOScriptLogic.executeScript(programmer.toRef(), where, "accept", whatRef);
            if ((val instanceof MOONumber) && !((MOONumber)val).toBoolean() && !programmer.isWizard())
                throw new MOOException("Destination refuses move");
        }
        else
            throw new MOOException("Destination refuses move");
        
        // check containment
        for (int cont = whereRef.getValue(); cont != -1; cont = MOODbLogic.get(cont).getLocation().getValue())
        {
            if (cont == whatRef.getValue())
                throw new MOOException("Move would cause containment loop");
        }
        
        if (!what.getLocation().isNone())
        {
            MOOObject whatParent = MOODbLogic.get(what.getLocation());
            whatParent.getChildren().remove((Integer)what.getOID());
            MOODbLogic.markDirty(whatParent.getOID());
            MOOScriptLogic.executeScriptMaybe(programmer.toRef(), whatParent, "exitfunc", whatRef);
        }
        
        what.setLocation(whereRef.getValue());
        MOODbLogic.markDirty(what.getOID());
        where.getContents().add(new MOONumber(what.getOID()));
        MOODbLogic.markDirty(where.getOID());

        MOOScriptLogic.executeScriptMaybe(programmer.toRef(), where, "enterfunc", whatRef);
    }
}
