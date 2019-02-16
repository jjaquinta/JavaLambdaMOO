package com.tsatsatzu.moo.core.api;

import com.tsatsatzu.moo.core.data.MOOConnection;
import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOOConnectionLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MOONetworkAPI
{
    /*
    Function: list connected_players ([include-all])
    Returns a list of the object numbers of those player objects with currently-active connections. 
    If include-all is provided and true, then the list includes the object numbers associated with all current connections, 
    including ones that are outbound and/or not yet logged-in. 
    */
    public static MOOList connected_players(MOONumber includeAll)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: int connected_seconds (obj player)
    Function: int idle_seconds (obj player)
    These functions return the number of seconds that the currently-active connection to player has existed and been idle, 
    respectively. If player is not the object number of a player object with a currently-active connection, then E_INVARG is raised. 
    */
    public static MOONumber connected_seconds(MOOObjRef player)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    public static MOONumber idle_seconds(MOOObjRef player)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: none notify (obj conn, str string [, no-flush])
    Enqueues string for output (on a line by itself) on the connection conn. If the programmer is not conn or a wizard, then 
    E_PERM is raised. If conn is not a currently-active connection, then this function does nothing. Output is normally written 
    to connections only between tasks, not during execution.

    The server will not queue an arbitrary amount of output for a connection; the MAX_QUEUED_OUTPUT compilation option 
    (in `options.h') controls the limit. When an attempt is made to enqueue output that would take the server over its limit, 
    it first tries to write as much output as possible to the connection without having to wait for the other end. If that doesn't 
    result in the new output being able to fit in the queue, the server starts throwing away the oldest lines in the queue until 
    the new ouput will fit. The server remembers how many lines of output it has `flushed' in this way and, when next it can 
    succeed in writing anything to the connection, it first writes a line like >> Network buffer overflow: X lines of output to 
    you have been lost << where X is the number of flushed lines.

    If no-flush is provided and true, then notify() never flushes any output from the queue; instead it immediately returns 
    false. Notify() otherwise always returns true. 
    */
    public static MOONumber notify(MOOObjRef ref, MOOString msg, MOONumber noFlush) throws MOOException
    {
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !programmer.equals(ref))
            throw new MOOException("You cannot write to that player");
        MOOConnection conn = MOOConnectionLogic.findConnection(ref);
        if (conn == null)
            return MOONumber.TRUE;
        conn.print(msg.getValue());
        if (noFlush.toBoolean())
            return MOONumber.FALSE;
        conn.flush();
        return MOONumber.TRUE;
    }

    /*
    Function: int buffered_output_length ([obj conn])
    Returns the number of bytes currently buffered for output to the connection conn. If conn is not provided, returns the maximum 
    number of bytes that will be buffered up for output on any connection. 
    */
    public static MOONumber buffered_output_length(MOOObjRef conn)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: str read ([obj conn [, non-blocking]])
    Reads and returns a line of input from the connection conn (or, if not provided, from the player that typed the command that 
    initiated the current task). If non-blocking is false or not provided, this function suspends the current task, resuming it when 
    there is input available to be read. If non-blocking is provided and true, this function never suspends the calling task; if 
    there is no input currently available for input, read() simply returns 0 immediately.

    If player is provided, then the programmer must either be a wizard or the owner of player; if player is not provided, then 
    read() may only be called by a wizard and only in the task that was last spawned by a command from the connection in question. 
    Otherwise, E_PERM is raised. If the given player is not currently connected and has no pending lines of input, or if the 
    connection is closed while a task is waiting for input but before any lines of input are received, then read() raises E_INVARG.

    The restriction on the use of read() without any arguments preserves the following simple invariant: if input is being read 
    from a player, it is for the task started by the last command that player typed. This invariant adds responsibility to the 
    programmer, however. If your program calls another verb before doing a read(), then either that verb must not suspend or else 
    you must arrange that no commands will be read from the connection in the meantime. The most straightforward way to do this 
    is to call

    set_connection_option(player, "hold-input", 1)

    before any task suspension could happen, then make all of your calls to read() and other code that might suspend, and 
    finally call

    set_connection_option(player, "hold-input", 0)

    to allow commands once again to be read and interpreted normally. 
    */
    public static MOOString read(MOOObjRef conn, MOONumber nonBlocking)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: none force_input (obj conn, str line [, at-front])
    Inserts the string line as an input task in the queue for the connection conn, just as if it had arrived as input over the 
    network. If at_front is provided and true, then the new line of input is put at the front of conn's queue, so that it will 
    be the very next line of input processed even if there is already some other input in that queue. Raises E_INVARG if conn 
    does not specify a current connection and E_PERM if the programmer is neither conn nor a wizard. 
    */
    public static void force_input(MOOObjRef conn, MOOString line, MOONumber nonBlocking)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: none flush_input (obj conn [show-messages])
    Performs the same actions as if the connection conn's defined flush command had been received on that connection, i.e., 
    removes all pending lines of input from conn's queue and, if show-messages is provided and true, prints a message to conn 
    listing the flushed lines, if any. See the chapter on server assumptions about the database for more information about a 
    connection's defined flush command. 
    */
    public static void flush_input(MOOObjRef conn, MOONumber showMessages)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: list output_delimiters (obj player)
    Returns a list of two strings, the current output prefix and output suffix for player. If player does not have an active 
    network connection, then E_INVARG is raised. If either string is currently undefined, the value "" is used instead. See 
    the discussion of the PREFIX and SUFFIX commands in the next chapter for more information about the output prefix and suffix. 
    */
    public static MOOList output_delimiters(MOOObjRef player)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: none boot_player (obj player)
    Marks for disconnection any currently-active connection to the given player. The connection will not actually be closed 
    until the currently-running task returns or suspends, but all MOO functions (such as notify(), connected_players(), and 
    the like) immediately behave as if the connection no longer exists. If the programmer is not either a wizard or the same 
    as player, then E_PERM is raised. If there is no currently-active connection to player, then this function does nothing.

    If there was a currently-active connection, then the following verb call is made when the connection is actually closed:

    $user_disconnected(player)

    It is not an error if this verb does not exist; the call is simply skipped. 
    */
    public static void boot_player(MOOObjRef player)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: str connection_name (obj player)
    Returns a network-specific string identifying the connection being used by the given player. If the programmer is not a 
    wizard and not player, then E_PERM is raised. If player is not currently connected, then E_INVARG is raised.

    For the TCP/IP networking configurations, for in-bound connections, the string has the form

    "port lport from host, port port"

    where lport is the decimal TCP listening port on which the connection arrived, host is either the name or decimal TCP 
    address of the host from which the player is connected, and port is the decimal TCP port of the connection on that host.

    For outbound TCP/IP connections, the string has the form

    "port lport to host, port port"

    where lport is the decimal local TCP port number from which the connection originated, host is either the name or decimal 
    TCP address of the host to which the connection was opened, and port is the decimal TCP port of the connection on that 
    host.

    For the System V `local' networking configuration, the string is the UNIX login name of the connecting user or, if no 
    such name can be found, something of the form

    "User #number"

    where number is a UNIX numeric user ID.

    For the other networking configurations, the string is the same for all connections and, thus, useless. 
    */
    public static MOOString connection_name(MOOObjRef player)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: none set_connection_option (obj conn, str option, value)
    Controls a number of optional behaviors associated the connection conn. Raises E_INVARG if conn does not specify a 
    current connection and E_PERM if the programmer is neither conn nor a wizard. The following values for option are currently 
    supported:

    "hold-input"
        If value is true, then input received on conn will never be treated as a command; instead, it will remain in the 
        queue until retrieved by a call to read(). 
    "client-echo"
        Send the Telnet Protocol `WONT ECHO' or `WILL ECHO' command, depending on whether value is true or false, respectively. 
        For clients that support the Telnet Protocol, this should toggle whether or not the client echoes locally the characters 
        typed by the user. Note that the server itself never echoes input characters under any circumstances. (This option is 
        only available under the TCP/IP networking configurations.) 
    "binary"
        If value is true, then both input from and output to conn can contain arbitrary bytes. Input from a connection in binary 
        mode is not broken into lines at all; it is delivered to either the read() function or the built-in command parser as 
        binary strings, in whatever size chunks come back from the operating system. (See the early section on MOO value types 
        for a description of the binary string representation.) For output to a connection in binary mode, the second argument 
        to `notify()' must be a binary string; if it is malformed, E_INVARG is raised. 
    "flush-command"
        If value is a non-empty string, then it becomes the new flush command for this connection, by which the player can 
        flush all queued input that has not yet been processed by the server. If value is not a non-empty string, then conn 
        is set to have no flush command at all. The default value of this option can be set via the property 
        $server_options.default_flush_command; see the chapter on server assumptions about the database for details. 
    */
    public static void set_connection_option(MOOObjRef conn, MOOString option, MOOValue value)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: list connection_options (obj conn)
    Returns a list of {name, value} pairs describing the current settings of all of the allowed options for the connection 
    conn. Raises E_INVARG if conn does not specify a current connection and E_PERM if the programmer is neither conn nor a 
    wizard. 
    */
    public static MOOList connection_options(MOOObjRef conn)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: value connection_option (obj conn, str name)
    Returns the current setting of the option name for the connection conn. Raises E_INVARG if conn does not specify a 
    current connection and E_PERM if the programmer is neither conn nor a wizard. 
    */
    public static MOOValue connection_option(MOOObjRef conn, MOOString name)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: obj open_network_connection (value, ...)
    Establishes a network connection to the place specified by the arguments and more-or-less pretends that a new, normal 
    player connection has been established from there. The new connection, as usual, will not be logged in initially and 
    will have a negative object number associated with it for use with read(), notify(), and boot_player(). This object 
    number is the value returned by this function.

    If the programmer is not a wizard or if the OUTBOUND_NETWORK compilation option was not used in building the server, 
    then E_PERM is raised. If the network connection cannot be made for some reason, then other errors will be returned, 
    depending upon the particular network implementation in use.

    For the TCP/IP network implementations (the only ones as of this writing that support outbound connections), there must 
    be two arguments, a string naming a host (possibly using the numeric Internet syntax) and an integer specifying a TCP 
    port. If a connection cannot be made because the host does not exist, the port does not exist, the host is not reachable 
    or refused the connection, E_INVARG is raised. If the connection cannot be made for other reasons, including resource 
    limitations, then E_QUOTA is raised.

    The outbound connection process involves certain steps that can take quite a long time, during which the server is not 
    doing anything else, including responding to user commands and executing MOO tasks. See the chapter on server assumptions 
    about the database for details about how the server limits the amount of time it will wait for these steps to successfully 
    complete.

    It is worth mentioning one tricky point concerning the use of this function. Since the server treats the new connection 
    pretty much like any normal player connection, it will naturally try to parse any input from that connection as commands 
    in the usual way. To prevent this treatment, you should use set_connection_option() to set the "hold-input" option true 
    on the connection. 
    */
    public static MOOObjRef open_network_connection(MOOValue value)
    {
        throw new IllegalStateException("Not implemented yet");
    }
    /*
    Function: value listen (obj object, point [, print-messages])
    Create a new point at which the server will listen for network connections, just as it does normally. Object is the 
    object whose verbs do_login_command, do_command, do_out_of_band_command, user_connected, user_created, user_reconnected, 
    user_disconnected, and user_client_disconnected will be called at appropriate points, just as these verbs are called on 
    #0 for normal connections. (See the chapter on server assumptions about the database for the complete story on when these 
    functions are called.) Point is a network-configuration-specific parameter describing the listening point. If print-messages 
    is provided and true, then the various database-configurable messages (also detailed in the chapter on server assumptions) 
    will be printed on connections received at the new listening point. Listen() returns canon, a `canonicalized' version of 
    point, with any configuration-specific defaulting or aliasing accounted for.

    This raises E_PERM if the programmer is not a wizard, E_INVARG if object is invalid or there is already a listening point 
    described by point, and E_QUOTA if some network-configuration-specific error occurred.

    For the TCP/IP configurations, point is a TCP port number on which to listen and canon is equal to point unless point is 
    zero, in which case canon is a port number assigned by the operating system.

    For the local multi-user configurations, point is the UNIX file name to be used as the connection point and canon is always 
    equal to point.

    In the single-user configuration, the can be only one listening point at a time; point can be any value at all and canon is 
    always zero. 
    */
    public static MOOValue listen(MOOObjRef object, MOOString point, MOONumber printMessages) throws MOOException
    {
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard())
            throw new MOOException("Only a wizard can listen");
        int canon = MOOConnectionLogic.listen(object, point.getValue());
        return new MOONumber(canon);
    }
    /*
    Function: none unlisten (canon)
    Stop listening for connections on the point described by canon, which should be the second element of some element of the 
    list returned by listeners(). Raises E_PERM if the programmer is not a wizard and E_INVARG if there does not exist a listener 
    with that description. 
    */
    public static void unlisten(MOOValue canon) throws MOOException
    {
        MOOConnectionLogic.terminate(((MOONumber)canon).getValue().intValue());
    }
    /*
    Function: list listeners ()
    Returns a list describing all existing listening points, including the default one set up automatically by the server when 
    it was started (unless that one has since been destroyed by a call to unlisten()). Each element of the list has the following form:

    {object, canon, print-messages}

    where object is the first argument given in the call to listen() to create this listening point, print-messages is true if 
    the third argument in that call was provided and true, and canon was the value returned by that call. (For the initial 
    listening point, object is #0, canon is determined by the command-line arguments or a network-configuration-specific default, 
    and print-messages is true.) 

    Please note that there is nothing special about the initial listening point created by the server when it starts; you can 
    use unlisten() on it just as if it had been created by listen(). This can be useful; for example, under one of the TCP/IP 
    configurations, you might start up your server on some obscure port, say 12345, connect to it by yourself for a while, and 
    then open it up to normal users by evaluating the statments

    unlisten(12345); listen(#0, 7777, 1)

     */
    public static MOOList listeners()
    {
        throw new IllegalStateException("Not implemented yet");
    }
}
