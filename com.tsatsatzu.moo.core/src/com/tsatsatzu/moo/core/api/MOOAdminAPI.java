package com.tsatsatzu.moo.core.api;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOOpsLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MOOAdminAPI
{
    /*
    Function: str server_version ()
    Returns a string giving the version number of the running MOO server. 
    */
    public static MOOString server_version() throws MOOException
    {
        return new MOOString(MOODbLogic.serverVersion());
    }
    /*
    Function: none server_log (str message [, is-error])
    The text in message is sent to the server log with a distinctive prefix (so that 
    it can be distinguished from server-generated messages). If the programmer is 
    not a wizard, then E_PERM is raised. If is-error is provided and true, then message 
    is marked in the server log as an error. 
    */
    public static void server_log(MOOString message, MOONumber isError) throws MOOException
    {
        throw new MOOException("Not implemented yet");
    }
    /*
    Function: obj renumber (obj object)
    The object number of the object currently numbered object is changed to be the least 
    nonnegative object number not currently in use and the new object number is returned. 
    If object is not valid, then E_INVARG is raised. If the programmer is not a wizard, 
    then E_PERM is raised. If there are no unused nonnegative object numbers less than object, 
    then object is returned and no changes take place.

    The references to object in the parent/children and location/contents hierarchies are 
    updated to use the new object number, and any verbs, properties and/or objects owned by 
    object are also changed to be owned by the new object number. The latter operation can 
    be quite time consuming if the database is large. No other changes to the database are 
    performed; in particular, no object references in property values or verb code are updated.

    This operation is intended for use in making new versions of the LambdaCore database from 
    the then-current LambdaMOO database, and other similar situations. Its use requires great care. 
    */
    public static MOOObjRef renumber(MOOObjRef object) throws MOOException
    {
        throw new MOOException("Not implemented yet");
    }
    /*
    Function: none reset_max_object ()
    The server's idea of the highest object number ever used is changed to be the highest object 
    number of a currently-existing object, thus allowing reuse of any higher numbers that refer 
    to now-recycled objects. If the programmer is not a wizard, then E_PERM is raised.

    This operation is intended for use in making new versions of the LambdaCore database from the 
    then-current LambdaMOO database, and other similar situations. Its use requires great care. 
    */
    public static void reset_max_object() throws MOOException
    {
        throw new MOOException("Not implemented yet");
    }
    /*
    Function: list memory_usage ()
    On some versions of the server, this returns statistics concerning the server consumption of 
    system memory. The result is a list of lists, each in the following format:

    {block-size, nused, nfree}

    where block-size is the size in bytes of a particular class of memory fragments, nused is the 
    number of such fragments currently in use in the server, and nfree is the number of such 
    fragments that have been reserved for use but are currently free.

    On servers for which such statistics are not available, memory_usage() returns {}. The compilation 
    option USE_GNU_MALLOC controls whether or not statistics are available; if the option is not provided, 
    statistics are not available. 
    */
    public static MOOList memory_usage() throws MOOException
    {
        return new MOOList();
    }
    /*
    Function: none dump_database ()
    Requests that the server checkpoint the database at its next opportunity. It is not normally necessary 
    to call this function; the server automatically checkpoints the database at regular intervals; see the 
    chapter on server assumptions about the database for details. If the programmer is not a wizard, 
    then E_PERM is raised. 
    */
    public static void dump_database() throws MOOException
    {
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if ((programmer == null) || !programmer.isWizard())
            throw new MOOException("You don't have permission for this");
        MOODbLogic.checkpoint();
    }
    /*
    Function: int db_disk_size ()
    Returns the total size, in bytes, of the most recent full representation of the database as one or more 
    disk files. Raises E_QUOTA if, for some reason, no such on-disk representation is currently available. 
    */
    public static MOONumber db_disk_size() throws MOOException
    {
        throw new MOOException("Not implemented yet");
    }
    /*
    Function: none shutdown ([str message])
    Requests that the server shut itself down at its next opportunity. Before doing so, a notice (incorporating 
    message, if provided) is printed to all connected players. If the programmer is not a wizard, then E_PERM 
    is raised. 
    */
    public static void shutdown(MOOString message) throws MOOException
    {
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if ((programmer == null) || !programmer.isWizard())
            throw new MOOException("You don't have permission for this");
        MOOList players = MOONetworkAPI.connected_players(MOONumber.FALSE);
        for (MOOValue p : players.getValue())
        {
            MOOObjRef player = (MOOObjRef)p;
            MOONetworkAPI.notify(player, message, MOONumber.FALSE);
        }
        MOOOpsLogic.startShutdown();
    }
}
