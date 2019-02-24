package com.tsatsatzu.moo.core.api;

import java.util.Date;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;

import jo.util.utils.obj.IntegerUtils;

public class MOOConvAPI
{
/*
 Function: int typeof (value)
    Takes any MOO value and returns an integer representing the type of value. The result is the same as the initial value of one of these built-in variables: INT, FLOAT, STR, LIST, OBJ, or ERR. Thus, one usually writes code like this:

    if (typeof(x) == LIST) ...

    and not like this:

    if (typeof(x) == 3) ...

    because the former is much more readable than the latter. 

*/
    public static MOONumber typeof(MOOValue val) throws MOOException
    {
        if (val instanceof MOOString)
            return new MOONumber(2);
        else if (val instanceof MOONumber)
            return new MOONumber(0);
        else if (val instanceof MOOObjRef)
            return new MOONumber(1);
        else if (val instanceof MOOList)
            return new MOONumber(4);
        else
            throw new MOOException("Cannot convert "+val.getClass().getName()+" to object");
    }
/*
    Function: str tostr (value, ...)
    Converts all of the given MOO values into strings and returns the concatenation of the results.

    tostr(17)                  =>   "17"
    tostr(1.0/3.0)             =>   "0.333333333333333"
    tostr(#17)                 =>   "#17"
    tostr("foo")               =>   "foo"
    tostr({1, 2})              =>   "{list}"
    tostr(E_PERM)              =>   "Permission denied"
    tostr("3 + 4 = ", 3 + 4)   =>   "3 + 4 = 7"

    Note that tostr() does not do a good job of converting lists into strings; all lists, including the empty list, are converted into the string "{list}". The function toliteral(), below, is better for this purpose. 

*/
    public static MOOString tostr(MOOValue val) throws MOOException
    {
        if (val instanceof MOOString)
            return (MOOString)val;
        else if (val instanceof MOONumber)
            return new MOOString(((MOONumber)val).getValue().toString());
        else if (val instanceof MOOObjRef)
            return new MOOString("#"+((MOOObjRef)val).getValue());
        else if (val instanceof MOOList)
        {
            StringBuffer sb = new StringBuffer();
            for (MOOValue v : ((MOOList)val).getValue())
            {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(tostr(v).getValue());
            }
            return new MOOString(sb.toString());
        }
        else
            throw new MOOException("Cannot convert "+val.getClass().getName()+" to object");
    }
/*
Function: str toliteral (value)
    Returns a string containing a MOO literal expression that, when evaluated, would be equal to value.

    toliteral(17)         =>   "17"
    toliteral(1.0/3.0)    =>   "0.333333333333333"
    toliteral(#17)        =>   "#17"
    toliteral("foo")      =>   "\"foo\""
    toliteral({1, 2})     =>   "{1, 2}"
    toliteral(E_PERM)     =>   "E_PERM"

*/
/*
Function: int toint (value)
Function: int tonum (value)
    Converts the given MOO value into an integer and returns that integer. Floating-point numbers are rounded toward zero, truncating their fractional parts. Object numbers are converted into the equivalent integers. Strings are parsed as the decimal encoding of a real number which is then converted to an integer. Errors are converted into integers obeying the same ordering (with respect to <= as the errors themselves. Toint() raises E_TYPE if value is a list. If value is a string but the string does not contain a syntactically-correct number, then toint() returns 0.

    toint(34.7)        =>   34
    toint(-34.7)       =>   -34
    toint(#34)         =>   34
    toint("34")        =>   34
    toint("34.7")      =>   34
    toint(" - 34  ")   =>   -34
    toint(E_TYPE)      =>   1

*/
    public static MOONumber toint(MOOValue val) throws MOOException
    {
        if (val instanceof MOOString)
        {
            String str = ((MOOString)val).getValue();
            if (str.startsWith("#"))
                return new MOONumber(IntegerUtils.parseInt(str.substring(1)));
            else
                return new MOONumber(IntegerUtils.parseInt(str));
        }
        else if (val instanceof MOONumber)
            return new MOONumber(((MOONumber)val).getValue().intValue());
        else if (val instanceof MOOObjRef)
            return new MOONumber(((MOOObjRef)val).getValue());
        else
            throw new MOOException("Cannot convert "+val.getClass().getName()+" to object");
    }
/*
Function: obj toobj (value)
    Converts the given MOO value into an object number and returns that object number. The conversions are very similar to those for toint() except that for strings, the number may be preceded by `#'.

    toobj("34")       =>   #34
    toobj("#34")      =>   #34
    toobj("foo")      =>   #0
    toobj({1, 2})     error-->   E_TYPE

*/
    public static MOOObjRef toobj(MOOValue val) throws MOOException
    {
        if (val instanceof MOOString)
        {
            String str = ((MOOString)val).getValue();
            if (str.startsWith("#"))
                return new MOOObjRef(IntegerUtils.parseInt(str.substring(1)));
            else
                return new MOOObjRef(IntegerUtils.parseInt(str));
        }
        else if (val instanceof MOONumber)
            return new MOOObjRef(((MOONumber)val).getValue().intValue());
        else if (val instanceof MOOObjRef)
            return (MOOObjRef)val;
        else
            throw new MOOException("Cannot convert "+val.getClass().getName()+" to object");
    }
/*
Function: float tofloat (value)
    Converts the given MOO value into a floating-point number and returns that number. Integers and object numbers are converted into the corresponding integral floating-point numbers. Strings are parsed as the decimal encoding of a real number which is then represented as closely as possible as a floating-point number. Errors are first converted to integers as in toint() and then converted as integers are. Tofloat() raises E_TYPE if value is a list. If value is a string but the string does not contain a syntactically-correct number, then tofloat() returns 0.

    tofloat(34)          =>   34.0
    tofloat(#34)         =>   34.0
    tofloat("34")        =>   34.0
    tofloat("34.7")      =>   34.7
    tofloat(E_TYPE)      =>   1.0

*/
/*
Function: int equals (value1, value2)
    Returns true if value1 is completely indistinguishable from value2. This is much the same operation as "value1 == value2" except that, unlike ==, the equals() function does not treat upper- and lower-case characters in strings as equal.

    "Foo" == "foo"         =>   1
    equals("Foo", "foo")   =>   0
    equals("Foo", "Foo")   =>   1

*/
/*
Function: int value_bytes (value)
    Returns the number of bytes of the server's memory required to store the given value. 

*/
/*
Function: str value_hash (value)
    Returns the same string as string_hash(toliteral(value)); see the description of string_hash() for details.  
 */

    /*
    Function: int time ()
    Returns the current time, represented as the number of seconds that have elapsed since midnight on 1 January 1970, Greenwich Mean Time. 
    */
    public static MOONumber time() throws MOOException
    {
        return new MOONumber((int)(System.currentTimeMillis()/1000L));
    }
    /*
    Function: str ctime ([int time])
    Interprets time as a time, using the same representation as given in the description of time(), above, and converts it into a 28-character, human-readable string in the following format:

    Mon Aug 13 19:13:20 1990 PDT

    If the current day of the month is less than 10, then an extra blank appears between the month and the day:

    Mon Apr  1 14:10:43 1991 PST

    If time is not provided, then the current time is used.

    Note that ctime() interprets time for the local time zone of the computer on which the MOO server is running. 
    */
    public static MOOString ctime(MOONumber tick) throws MOOException
    {
        Date d;
        if (tick != null)
            d = new Date(tick.getValue().intValue()*1000L);
        else
            d = new Date();
        return new MOOString(d.toString());
    }
}
