package com.tsatsatzu.moo.core.api;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MOOPlayerAPI
{
    /*
     list players ()
     Returns a list of the object numbers of all player objects in the database. 
     */
    public static MOOList players()
    {
        MOOList players = new MOOList();
        for (int oid : MOODbLogic.getPlayers())
            players.add(oid);
        return players;
    }

    /*
    Function: int is_player (obj object)
    Returns a true value if the given object is a player object and a false value otherwise. If object is not valid, E_INVARG is raised.
    */
    public static MOONumber is_player(MOOObjRef obj)
    {
        if (obj.isNone())
            return MOONumber.FALSE;
        MOOObject o = MOODbLogic.get(obj);
        if (o == null)
            return MOONumber.FALSE;
        if (o.isPlayer())
            return MOONumber.TRUE;
        else
            return MOONumber.FALSE;
    }
    
    /*
    Function: none set_player_flag (obj object, value)
    Confers or removes the "player object" status of the given object, depending upon the truth value of value. 
    If object is not valid, E_INVARG is raised. If the programmer is not a wizard, then E_PERM is raised.

    If value is true, then object gains (or keeps) "player object" status: it will be an element of the list 
    returned by players(), the expression is_player(object) will return true, and the server will treat a call 
    to $do_login_command() that returns object as logging in the current connection.

    If value is false, the object loses (or continues to lack) "player object" status: it will not be an element 
    of the list returned by players(), the expression is_player(object) will return false, and users cannot connect 
    to object by name when they log into the server. In addition, if a user is connected to object at the time that 
    it loses "player object" status, then that connection is immediately broken, just as if boot_player(object) had 
    been called (see the description of boot_player() below). 
     */
    public static void set_player_flag(MOOObjRef ref, MOONumber value) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(ref);
        if (obj == null)
            throw new MOOException("No such object "+ref);
        if (!MOOProgrammerLogic.getProgrammer().isWizard())
            throw new MOOException("Only wizard can set player.");
        obj.setPlayer(true);
        MOODbLogic.markDirty(ref);
    }
}
