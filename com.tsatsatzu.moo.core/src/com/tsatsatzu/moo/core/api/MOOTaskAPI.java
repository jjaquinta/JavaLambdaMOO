package com.tsatsatzu.moo.core.api;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;

public class MOOTaskAPI
{
    /*
    Function: none raise (code [, str message [, value]])
    Raises code as an error in the same way as other MOO expressions, statements, and functions do. 
    Message, which defaults to the value of tostr(code), and value, which defaults to zero, are made 
    available to any try-except statements that catch the error. If the error is not caught, then 
    message will appear on the first line of the traceback printed to the user. 
    */
    public static void raise(MOOString code, MOOString message, MOOValue value) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: value call_function (str func-name, arg, ...)
    Calls the built-in function named func-name, passing the given arguments, and returns whatever 
    that function returns. Raises E_INVARG if func-name is not recognized as the name of a known 
    built-in function. This allows you to compute the name of the function to call and, in particular, 
    allows you to write a call to a built-in function that may or may not exist in the particular 
    version of the server you're using. 
    */
    public static MOOValue call_function(MOOString funcName, MOOValue... arg) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: list function_info ([str name])
    Returns descriptions of the built-in functions available on the server. If name is provided, only 
    the description of the function with that name is returned. If name is omitted, a list of 
    descriptions is returned, one for each function available on the server. Raised E_INVARG if name 
    is provided but no function with that name is available on the server.

    Each function description is a list of the following form:

    {name, min-args, max-args, types

    where name is the name of the built-in function, min-args is the minimum number of arguments that 
    must be provided to the function, max-args is the maximum number of arguments that can be provided 
    to the function or -1 if there is no maximum, and types is a list of max-args integers (or min-args 
    if max-args is -1), each of which represents the type of argument required in the corresponding 
    position. Each type number is as would be returned from the typeof() built-in function except that 
    -1 indicates that any type of value is acceptable and -2 indicates that either integers or 
    floating-point numbers may be given. For example, here are several entries from the list:

    {"listdelete", 2, 2, {4, 0}}
    {"suspend", 0, 1, {0}}
    {"server_log", 1, 2, {2, -1}}
    {"max", 1, -1, {-2}}
    {"tostr", 0, -1, {}}

    Listdelete() takes exactly 2 arguments, of which the first must be a list (LIST == 4) and the second 
    must be an integer (INT == 0). Suspend() has one optional argument that, if provided, must be an 
    integer. Server_log() has one required argument that must be a string (STR == 2) and one optional 
    argument that, if provided, may be of any type. Max() requires at least one argument but can take any 
    number above that, and the first argument must be either an integer or a floating-point number; the 
    type(s) required for any other arguments can't be determined from this description. Finally, tostr() 
    takes any number of arguments at all, but it can't be determined from this description which argument 
    types would be acceptable in which positions. 
    */
    public static MOOList function_info(MOOString name) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: list eval (str string)
    The MOO-code compiler processes string as if it were to be the program associated with some verb and, 
    if no errors are found, that fictional verb is invoked. If the programmer is not, in fact, a programmer, 
    then E_PERM is raised. The normal result of calling eval() is a two element list. The first element is 
    true if there were no compilation errors and false otherwise. The second element is either the result 
    returned from the fictional verb (if there were no compilation errors) or a list of the compiler's 
    error messages (otherwise).

    When the fictional verb is invoked, the various built-in variables have values as shown below:

    player    the same as in the calling verb
    this      #-1
    caller    the same as the initial value of this in the calling verb

    args      {}
    argstr    ""

    verb      ""
    dobjstr   ""
    dobj      #-1
    prepstr   ""
    iobjstr   ""
    iobj      #-1

    The fictional verb runs with the permissions of the programmer and as if its `d' permissions bit were on.

    eval("return 3 + 4;")   =>   {1, 7}
    */
    public static MOOList eval(MOOString code) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: none set_task_perms (obj who)
    Changes the permissions with which the currently-executing verb is running to be those of who. If the 
    programmer is neither who nor a wizard, then E_PERM is raised.

        Note: This does not change the owner of the currently-running verb, only the permissions of this 
        particular invocation. It is used in verbs owned by wizards to make themselves run with lesser 
        (usually non-wizard) permissions. 
    */
    public static void set_task_perms(MOOObjRef who) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: obj caller_perms ()
    Returns the permissions in use by the verb that called the currently-executing verb. If the 
    currently-executing verb was not called by another verb (i.e., it is the first verb called in a command 
    or server task), then caller_perms() returns #-1. 
    */
    public static MOOObjRef caller_perms() throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: int ticks_left ()
    Function: int seconds_left ()
    These two functions return the number of ticks or seconds (respectively) left to the current task before 
    it will be forcibly terminated. These are useful, for example, in deciding when to call `suspend()' to 
    continue a long-lived computation. 
    */
    public static MOONumber ticks_left() throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    public static MOONumber seconds_left() throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: int task_id ()
    Returns the non-zero, non-negative integer identifier for the currently-executing task. Such integers 
    are randomly selected for each task and can therefore safely be used in circumstances where unpredictability 
    is required. 
    */
    public static MOONumber task_id() throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: value suspend ([int seconds])
    Suspends the current task, and resumes it after at least seconds seconds. (If seconds is not provided, 
    the task is suspended indefinitely; such a task can only be resumed by use of the resume() function.) When 
    the task is resumed, it will have a full quota of ticks and seconds. This function is useful for programs 
    that run for a long time or require a lot of ticks. If seconds is negative, then E_INVARG is raised. Suspend() 
    returns zero unless it was resumed via resume(), in which case it returns the second argument given to that function.

    In some sense, this function forks the `rest' of the executing task. However, there is a major difference 
    between the use of `suspend(seconds)' and the use of the `fork (seconds)'. The `fork' statement creates a 
    new task (a forked task) while the currently-running task still goes on to completion, but a suspend() suspends 
    the currently-running task (thus making it into a suspended task). This difference may be best explained by the 
    following examples, in which one verb calls another:

    .program   #0:caller_A
    #0.prop = 1;
    #0:callee_A();
    #0.prop = 2;
    .

    .program   #0:callee_A
    fork(5)
      #0.prop = 3;
    endfork
    .

    .program   #0:caller_B
    #0.prop = 1;
    #0:callee_B();
    #0.prop = 2;
    .

    .program   #0:callee_B
    suspend(5);
    #0.prop = 3;
    .

    Consider #0:caller_A, which calls #0:callee_A. Such a task would assign 1 to #0.prop, call #0:callee_A, fork a new 
    task, return to #0:caller_A, and assign 2 to #0.prop, ending this task. Five seconds later, if the forked task had 
    not been killed, then it would begin to run; it would assign 3 to #0.prop and then stop. So, the final value of 
    #0.prop (i.e., the value after more than 5 seconds) would be 3.

    Now consider #0:caller_B, which calls #0:callee_B instead of #0:callee_A. This task would assign 1 to #0.prop, 
    call #0:callee_B, and suspend. Five seconds later, if the suspended task had not been killed, then it would resume; 
    it would assign 3 to #0.prop, return to #0:caller_B, and assign 2 to #0.prop, ending the task. So, the final value 
    of #0.prop (i.e., the value after more than 5 seconds) would be 2.

    A suspended task, like a forked task, can be described by the queued_tasks() function and killed by the kill_task() 
    function. Suspending a task does not change its task id. A task can be suspended again and again by successive calls 
    to suspend().

    By default, there is no limit to the number of tasks any player may suspend, but such a limit can be imposed from 
    within the database. See the chapter on server assumptions about the database for details. 
    */
    public static MOOValue suspend(MOONumber seconds) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: none resume (int task-id [, value])
    Immediately ends the suspension of the suspended task with the given task-id; that task's call to suspend() will 
    return value, which defaults to zero. Resume() raises E_INVARG if task-id does not specify an existing suspended 
    task and E_PERM if the programmer is neither a wizard nor the owner of the specified task. 
    */
    public static void resume(MOONumber taskID, MOOValue value) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: list queue_info ([obj player])
    If player is omitted, returns a list of object numbers naming all players that currently have active task queues 
    inside the server. If player is provided, returns the number of background tasks currently queued for that user. 
    It is guaranteed that queue_info(X) will return zero for any X not in the result of queue_info(). 
    */
    public static MOOList queue_info(MOOObjRef player) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: list queued_tasks ()
    Returns information on each of the background tasks (i.e., forked, suspended or reading) owned by the programmer 
    (or, if the programmer is a wizard, all queued tasks). The returned value is a list of lists, each of which encodes 
    certain information about a particular queued task in the following format:

    {task-id, start-time, x, y,
     programmer, verb-loc, verb-name, line, this}

    where task-id is an integer identifier for this queued task, start-time is the time after which this task will 
    begin execution (in time() format), x and y are obsolete values that are no longer interesting, programmer is the 
    permissions with which this task will begin execution (and also the player who owns this task), verb-loc is the 
    object on which the verb that forked this task was defined at the time, verb-name is that name of that verb, line 
    is the number of the first line of the code in that verb that this task will execute, and this is the value of the 
    variable `this' in that verb. For reading tasks, start-time is -1.

    The x and y fields are now obsolete and are retained only for backward-compatibility reasons. They may be reused 
    for new purposes in some future version of the server. 
    */
    public static MOOList queued_tasks() throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: none kill_task (int task-id)
    Removes the task with the given task-id from the queue of waiting tasks. If the programmer is not the owner of 
    that task and not a wizard, then E_PERM is raised. If there is no task on the queue with the given task-id, 
    then E_INVARG is raised. 
    */
    public static void kill_task(MOONumber includeAll) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: list callers ([include-line-numbers])
    Returns information on each of the verbs and built-in functions currently waiting to resume execution in the 
    current task. When one verb or function calls another verb or function, execution of the caller is temporarily 
    suspended, pending the called verb or function returning a value. At any given time, there could be several 
    such pending verbs and functions: the one that called the currently executing verb, the verb or function that 
    called that one, and so on. The result of callers() is a list, each element of which gives information about 
    one pending verb or function in the following format:

    {this, verb-name, programmer, verb-loc, player, line-number}

    For verbs, this is the initial value of the variable `this' in that verb, verb-name is the name used to invoke 
    that verb, programmer is the player with whose permissions that verb is running, verb-loc is the object on which 
    that verb is defined, player is the initial value of the variable `player' in that verb, and line-number indicates 
    which line of the verb's code is executing. The line-number element is included only if the include-line-numbers 
    argument was provided and true.

    For functions, this, programmer, and verb-loc are all #-1, verb-name is the name of the function, and line-number 
    is an index used internally to determine the current state of the built-in function. The simplest correct test 
    for a built-in function entry is

    (VERB-LOC == #-1  &&  PROGRAMMER == #-1  &&  VERB-NAME != "")

    The first element of the list returned by callers() gives information on the verb that called the currently-executing 
    verb, the second element describes the verb that called that one, and so on. The last element of the list describes 
    the first verb called in this task. 
    */
    public static MOOList callers(MOONumber includeLineNumbers) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
    /*
    Function: list task_stack (int task-id [, include-line-numbers])
    Returns information like that returned by the callers() function, but for the suspended task with the given task-id; 
    the include-line-numbers argument has the same meaning as in callers(). Raises E_INVARG if task-id does not specify 
    an existing suspended task and E_PERM if the programmer is neither a wizard nor the owner of the specified task. 
    */
    public static MOOList task_stack(MOONumber taskID, MOONumber inlcudeLineNumbers) throws MOOException
    {
        throw new MOOException("Not implemented yet.");
    }
}
