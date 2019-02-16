package com.tsatsatzu.moo.core.api;

import java.util.ArrayList;
import java.util.List;

import com.tsatsatzu.moo.core.data.MOOException;
import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;
import com.tsatsatzu.moo.core.logic.MOODbLogic;
import com.tsatsatzu.moo.core.logic.MOOProgrammerLogic;

public class MOOVerbAPI
{
    /*
        Returns a list of the names of the verbs defined directly on the given object, 
        not inherited from its parent. If object is not valid, then E_INVARG is raised. 
        If the programmer does not have read permission on object, then E_PERM is raised. 

        Most of the remaining operations on verbs accept a string containing the verb's 
        name to identify the verb in question. Because verbs can have multiple names and 
        because an object can have multiple verbs with the same name, this practice can 
        lead to difficulties. To most unambiguously refer to a particular verb, one can 
        instead use a positive integer, the index of the verb in the list returned by 
        verbs(), described above.

        For example, suppose that verbs(#34) returns this list:
        {"foo", "bar", "baz", "foo"}
        Object #34 has two verbs named `foo' defined on it (this may not be an error, 
        if the two verbs have different command syntaxes). To refer unambiguously to the 
        first one in the list, one uses the integer 1; to refer to the other one, one uses 4.

        In the function descriptions below, an argument named verb-desc is either a string 
        containing the name of a verb or else a positive integer giving the index of that 
        verb in its defining object's verbs() list.
    */
    public static MOOList verbs(MOOObjRef objRef) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOList verbs = new MOOList();
        for (MOOVerb verb : obj.getVerbs())
            verbs.getValue().add(new MOOString(verb.getName()));
        return verbs;
    }
    
    private static MOOVerb findVerb(MOOObject obj, MOOValue verbDesc) throws MOOException
    {
        if (verbDesc instanceof MOONumber)
        {
            int idx = ((MOONumber)verbDesc).getValue().intValue();
            if ((idx < 0) || (idx >= obj.getVerbs().size()))
                throw new MOOException("Verb index "+verbDesc+" out of bounds");
            return obj.getVerbs().get(idx);
        }
        else if (verbDesc instanceof MOOString)
        {
            String name = ((MOOString)verbDesc).getValue();
            for (MOOVerb verb : obj.getVerbs())
                if (verb.getName().equals(name))
                    return verb;
            throw new MOOException("No verb with name "+verbDesc+" found");
        }
        else 
            throw new MOOException("Unknown verb description type "+verbDesc.getClass().getName());
    }
    
    /*
        These two functions get and set (respectively) the owner, permission bits, and name(s) 
        for the verb as specified by verb-desc on the given object. If object is not valid, 
        then E_INVARG is raised. If object does not define a verb as specified by verb-desc, 
        then E_VERBNF is raised. If the programmer does not have read (write) permission on 
        the verb in question, then verb_info() (set_verb_info()) raises E_PERM. Verb info has the 
        following form:
            {owner, perms, names}
        where owner is an object, perms is a string containing only characters from the set 
        `r', `w', `x', and `d', and names is a string. This is the kind of value returned by 
        verb_info() and expected as the third argument to set_verb_info(). set_verb_info() 
        raises E_INVARG if owner is not valid, if perms contains any illegal characters, or 
        if names is the empty string or consists entirely of spaces; it raises E_PERM if owner 
        is not the programmer and the programmer is not a wizard.      
    */
    public static MOOList verb_info(MOOObjRef objRef, MOOValue verbDesc) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isRead() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on obj="+objRef);
        MOOList info = new MOOList();
        info.getValue().add(verb.getOwner());
        String perms = "";
        if (verb.isRead())
            perms += "r";
        if (verb.isWrite())
            perms += "w";
        if (verb.isExecute())
            perms += "x";
        info.getValue().add(new MOOString(perms));
        info.getValue().add(new MOOString(verb.getName()));        
        return info;
    }
    
    public static void verb_set_info(MOOObjRef objRef, MOOValue verbDesc, MOOList info) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isWrite() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        VerbInfo vi = parseVerbInfo(info);
        verb.setOwner(vi.ownerRef);
        verb.setRead(vi.read);
        verb.setWrite(vi.write);
        verb.setExecute(vi.execute);
        verb.setName(vi.name);
        MOODbLogic.markDirty(objRef);
    }
    
    /*
    These two functions get and set (respectively) the direct-object, preposition, and indirect-object 
    specifications for the verb as specified by verb-desc on the given object. If object is not valid, 
    then E_INVARG is raised. If object does not define a verb as specified by verb-desc, then E_VERBNF 
    is raised. If the programmer does not have read (write) permission on the verb in question, 
    then verb_args() (set_verb_args()) raises E_PERM. Verb args specifications have the following form:

    {dobj, prep, iobj}

    where dobj and iobj are strings drawn from the set "this", "none", and "any", and prep is a string 
    that is either "none", "any", or one of the prepositional phrases listed much earlier in the 
    description of verbs in the first chapter. This is the kind of value returned by verb_args() 
    and expected as the third argument to set_verb_args(). Note that for set_verb_args(), prep must 
    be only one of the prepositional phrases, not (as is shown in that table) a set of such phrases 
    separated by `/' characters. set_verb_args raises E_INVARG if any of the dobj, prep, or iobj 
    strings is illegal.

    verb_args($container, "take")
                    =>   {"any", "out of/from inside/from", "this"}
    set_verb_args($container, "take", {"any", "from", "this"})

     */

    public static MOOList verb_args(MOOObjRef objRef, MOOValue verbDesc) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isRead() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on obj="+objRef);
        MOOList args = new MOOList();
        args.getValue().add(new MOOString(MOOVerb.objTypeToStr(verb.getDirectObjectType())));
        args.getValue().add(new MOOString(MOOVerb.prepTypeToStr(verb.getPrepositionType())));
        args.getValue().add(new MOOString(MOOVerb.objTypeToStr(verb.getIndirectObjectType())));
        return args;
    }
    
    public static void verb_set_args(MOOObjRef objRef, MOOValue verbDesc, MOOList args) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isWrite() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        VerbArgs va = parseVerbArgs(args);
        verb.setDirectObjectType(va.directObject);
        verb.setPrepositionType(va.prep);
        verb.setIndirectObjectType(va.indirectObject);
        MOODbLogic.markDirty(objRef);
    }
    
    /*
    Defines a new verb on the given object. The new verb's owner, permission bits and name(s) are 
    given by info in the same format as is returned by verb_info(), described above. The new verb's 
    direct-object, preposition, and indirect-object specifications are given by args in the same 
    format as is returned by verb_args, described above. The new verb initially has the empty program 
    associated with it; this program does nothing but return an unspecified value.

    If object is not valid, or info does not specify a valid owner and well-formed permission bits 
    and verb names, or args is not a legitimate syntax specification, then E_INVARG is raised. If 
    the programmer does not have write permission on object or if the owner specified by info is not 
    the programmer and the programmer is not a wizard, then E_PERM is raised.      
    */
    
    public static void add_verb(MOOObjRef objRef, MOOList info, MOOList args) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !obj.isWrite() && !obj.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        VerbInfo vi = parseVerbInfo(info);
        VerbArgs va = parseVerbArgs(args);
        MOOVerb verb = new MOOVerb();
        verb.setOwner(vi.ownerRef);
        verb.setRead(vi.read);
        verb.setWrite(vi.write);
        verb.setExecute(vi.execute);
        verb.setName(vi.name);
        verb.setDirectObjectType(va.directObject);
        verb.setPrepositionType(va.prep);
        verb.setIndirectObjectType(va.indirectObject);
        obj.getVerbs().add(verb);
        MOODbLogic.markDirty(objRef);
    }
    
    /*
    Removes the verb as specified by verb-desc from the given object. If object is not valid, then 
    E_INVARG is raised. If the programmer does not have write permission on object, then E_PERM is 
    raised. If object does not define a verb as specified by verb-desc, then E_VERBNF is raised.  
    */
    public static void delete_verb(MOOObjRef objRef, MOOValue verbDesc) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isWrite() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        obj.getVerbs().remove(verb);
        MOODbLogic.markDirty(objRef);
    }
    
    /*
    These functions get and set (respectively) the MOO-code program associated with the verb 
    as specified by verb-desc on object. The program is represented as a list of strings, one 
    for each line of the program; this is the kind of value returned by verb_code() and expected 
    as the third argument to set_verb_code(). For verb_code(), the expressions in the returned 
    code are usually written with the minimum-necessary parenthesization; if full-paren is true, 
    then all expressions are fully parenthesized. Also for verb_code(), the lines in the returned 
    code are usually not indented at all; if indent is true, each line is indented to better show 
    the nesting of statements.

    If object is not valid, then E_INVARG is raised. If object does not define a verb as specified 
    by verb-desc, then E_VERBNF is raised. If the programmer does not have read (write) permission 
    on the verb in question, then verb_code() (set_verb_code()) raises E_PERM. If the programmer is 
    not, in fact. a programmer, then E_PERM is raised.

    For set_verb_code(), the result is a list of strings, the error messages generated by the MOO-code 
    compiler during processing of code. If the list is non-empty, then set_verb_code() did not install 
    code; the program associated with the verb in question is unchanged.  
    */
    public static MOOList verb_code(MOOObjRef objRef, MOOValue verbDesc, MOONumber fully_parent, MOONumber indent) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isRead() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no read permission on obj="+objRef);
        MOOList code = new MOOList();
        for (String line : verb.getScript())
            code.getValue().add(new MOOString(line));
        return code;
    }
    
    public static MOOList set_verb_code(MOOObjRef objRef, MOOValue verbDesc, MOOList code) throws MOOException
    {
        MOOObject obj = MOODbLogic.get(objRef);
        if (obj == null)
            throw new MOOException("Invalid arg="+objRef);
        MOOVerb verb = findVerb(obj, verbDesc);
        MOOObject programmer = MOOProgrammerLogic.getProgrammer();
        if (!programmer.isWizard() && !verb.isWrite() && !verb.getOwner().equals(programmer))
            throw new MOOException(programmer+" has no write permission on obj="+objRef);
        List<String> script = new ArrayList<>();
        MOOList err = new MOOList();
        for (int i = 0; i < code.getValue().size(); i++)
        {
            MOOValue line = code.getValue().get(i);
            if (line instanceof MOOString)
                script.add(((MOOString)line).getValue());
            else
                err.getValue().add(new MOOString("Line "+(i+1)+" is not a string"));
        }
        if (err.getValue().size() == 0)
            verb.setScript(script);        
        return err;
    }
    
    private static VerbInfo parseVerbInfo(MOOList info) throws MOOException
    {
        VerbInfo vi = new VerbInfo();
        if (info.getValue().size() != 3)
            throw new MOOException("Expected info to contain three elements, not "+info.getValue().size());
        if (!(info.getValue().get(0) instanceof MOOObjRef))
            throw new MOOException("Expected first elements of info to be an object, not "+info.getValue().get(0));
        vi.ownerRef = (MOOObjRef)info.getValue().get(0);
        MOOObject owner = MOODbLogic.get(vi.ownerRef);
        if (owner == null)
            throw new MOOException("Invalid owner specified "+vi.ownerRef);
        if (!(info.getValue().get(1) instanceof MOOString))
            throw new MOOException("Expected first elements of info to be an string, not "+info.getValue().get(1));
        vi.read = false;
        vi.write = false;
        vi.execute = false;
        for (char ch : ((MOOString)info.getValue().get(1)).getValue().toCharArray())
            if (ch == 'r')
                vi.read = true;
            else if (ch == 'w')
                vi.write = true;
            else if (ch == 'x')
                vi.execute = true;
            else
                throw new MOOException("Unexpected permission parameter in "+info.getValue().get(1));
        if (!(info.getValue().get(2) instanceof MOOString))
            throw new MOOException("Expected first elements of info to be an string, not "+info.getValue().get(2));
        vi.name = ((MOOString)info.getValue().get(2)).getValue();
        if (vi.name.trim().length() == 0)
            throw new MOOException("Illegal verb name '"+vi.name+"'");
        return vi;
    }
    
    private static VerbArgs parseVerbArgs(MOOList args) throws MOOException
    {
        VerbArgs va = new VerbArgs();
        if (args.getValue().size() != 3)
            throw new MOOException("Expected args to contain three elements, not "+args.getValue().size());
        if (!(args.getValue().get(0) instanceof MOOString))
            throw new MOOException("Expected first element of args to be an string, not "+args.getValue().get(0));
        va.directObject = MOOVerb.objStrToType(((MOOString)args.getValue().get(0)).getValue());
        if (!(args.getValue().get(1) instanceof MOOString))
            throw new MOOException("Expected second element of args to be an string, not "+args.getValue().get(1));
        va.prep = MOOVerb.prepStrToType(((MOOString)args.getValue().get(1)).getValue());
        if (!(args.getValue().get(2) instanceof MOOString))
            throw new MOOException("Expected firthirdst element of args to be an string, not "+args.getValue().get(2));
        va.indirectObject = MOOVerb.objStrToType(((MOOString)args.getValue().get(2)).getValue());
        return va;
    }
}

class VerbInfo
{
    MOOObjRef ownerRef;
    boolean read;
    boolean write;
    boolean execute;
    String name;
}

class VerbArgs
{
    int directObject;
    int prep;
    int indirectObject;   
}