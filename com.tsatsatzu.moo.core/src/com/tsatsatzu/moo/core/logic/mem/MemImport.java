package com.tsatsatzu.moo.core.logic.mem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tsatsatzu.moo.core.data.MOOObject;
import com.tsatsatzu.moo.core.data.MOOProperty;
import com.tsatsatzu.moo.core.data.MOOValue;
import com.tsatsatzu.moo.core.data.MOOVerb;
import com.tsatsatzu.moo.core.data.val.MOOList;
import com.tsatsatzu.moo.core.data.val.MOONumber;
import com.tsatsatzu.moo.core.data.val.MOOObjRef;
import com.tsatsatzu.moo.core.data.val.MOOString;

import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;

public class MemImport
{
    private static final int OF_player      = 0x01;
    private static final int OF_programmer  = 0x02;
    private static final int OF_wizard      = 0x04;
    private static final int OF_read        = 0x10;
    private static final int OF_write       = 0x20;
    private static final int OF_fertile     = 0x80;

    private static final int VP_read        = 0x01;
    private static final int VP_write       = 0x02;
    private static final int VP_execute     = 0x04;
    //private static final int VP_debug       = 0x08;
    private static final int VP_dobj        = 0x30; // ("none", "any", "this")
    private static final int VP_dobj_none   = 0x00;
    private static final int VP_dobj_any    = 0x10;
    private static final int VP_dobj_this   = 0x20;
    private static final int VP_iobj        = 0xC0; // ("none", "any", "this")
    private static final int VP_iobj_none   = 0x00;
    private static final int VP_iobj_any    = 0x40;
    private static final int VP_iobj_this   = 0x80;

    private static final int OP_read        = 0x01;
    private static final int OP_write       = 0x02;
    private static final int OP_chown       = 0x04;
    
    public static void importFromDisk(MemStore store, File disk) throws IOException
    {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(disk));
        int[] headers = readHeaders(store, is);
        int totalVerbs = headers[0];
        int totalPlayers = headers[1];
        readPlayers(store, is, totalPlayers);
        readObjects(store, is);
        readVerbs(store, is, totalVerbs);
        is.close();
    }
    
    private static void readVerbs(MemStore store, BufferedInputStream is, int totalVerbs) throws IOException
    {
        // Following the object descriptions is verb program code for every programmed verb. 
        // Unprogrammed verbs are omitted.
        System.out.println("Total Coded Verbs: "+totalVerbs);
        for (int i = 0; i < totalVerbs; i++)
        {
            /// For each program:
            // a tag line containing a pound sign, the object number where this verb resides, a colon, 
            // and a zero-based index into the object's defined verbs specifying the verb associated with the following code.
            String line = readLine(is);
            if (!line.startsWith("#"))
                throw new IllegalStateException("Expected # at start");
            int o = line.indexOf(':');
            if (o < 0)
                throw new IllegalStateException("Expected : in line");
            int oid = IntegerUtils.parseInt(line.substring(1, o));
            int idx = IntegerUtils.parseInt(line.substring(o + 1));
            MOOObject obj = store.getCache().get(oid);
            MOOVerb verb = obj.getVerbs().get(idx);
            System.out.println("  #"+obj.getOID()+":"+verb.getName());
            List<String> code = new ArrayList<>();
            for (;;)
            {
                line = readLine(is);
                if (line.contentEquals("."))
                    break;
                code.add(line);
            }
            verb.setScript(code);
        }
    }

    private static void readObjects(MemStore store, BufferedInputStream is) throws IOException
    {
        Map<Integer, List<MOOProperty>> definedPropIndex = new HashMap<>();
        Map<Integer, List<DeferredPropValue>> propValueIndex = new HashMap<>();
        Map<Integer, Integer> locationIndex = new HashMap<>();
        Map<Integer, Integer> parentIndex = new HashMap<>();
        System.out.println("Reading "+store.getMaxObject()+" objects");
        // The next section contains a description of every object in the database. Each object has the following structure: 
        for (int i = 0; i <= store.getMaxObject(); i++)
        {
            // a tag line containing a pound sign and the object number. If the object has been recycled, the word "recycled" also appears. 
            String line = readLine(is);
            if (line.indexOf("recycled") >= 0)
                continue;
            System.out.println("Reading object "+line);
            MOOObject obj = new MOOObject();
            obj.setOID(IntegerUtils.parseInt(line.substring(1)));
            store.getCache().put(obj.getOID(), obj);
            // a string containing the value of the object's .name property; 
            String name = readLine(is);
            setDefaultProperty(obj, MOOObject.PROP_NAME, new MOOString(name));
            System.out.println("  name="+name);
            // a string which can be ignored; 
            readLine(is);
            // a number containing a set of flags, described below
            int of = readInt(is);
            if ((of&OF_player) != 0)
                obj.setPlayer(true);
            if ((of&OF_programmer) != 0)
                setDefaultProperty(obj, MOOObject.PROP_PROGRAMMER, MOONumber.TRUE);
            if ((of&OF_wizard) != 0)
                setDefaultProperty(obj, MOOObject.PROP_WIZARD, MOONumber.TRUE);
            if ((of&OF_read) != 0)
                setDefaultProperty(obj, MOOObject.PROP_READ, MOONumber.TRUE);
            if ((of&OF_write) != 0)
                setDefaultProperty(obj, MOOObject.PROP_WRITE, MOONumber.TRUE);
            if ((of&OF_fertile) != 0)
                setDefaultProperty(obj, MOOObject.PROP_FERTILE, MOONumber.TRUE);
            // the object number which is this object's .owner; 
            MOOObjRef owner = readObj(is);
            setDefaultProperty(obj, MOOObject.PROP_OWNER, owner);
            System.out.println("  owner="+owner);
            // the object number which is this object's .location; 
            MOOObjRef location = readObj(is);
            setDefaultProperty(obj, MOOObject.PROP_LOCATION, location);
            if (!location.isNone())
                locationIndex.put(obj.getOID(), location.getValue());
            System.out.println("  location="+location);
            //  the object number of the first object in a linked list of this object's .contents (or -1 if it contains nothing);
            // the object number of the next object in a linked list of this object's .location's .contents (or -1 if the end of the list);
            readObj(is);
            readObj(is);
//            MOOList contents = new MOOList();
//            for (;;)
//            {
//                MOOObjRef o = readObj(is);
//                if (o.isNone())
//                    break;
//                contents.add(o);
//            }
//            setDefaultProperty(obj, MOOObject.PROP_CONTENTS, contents);
//            System.out.println("  contents size="+contents.getValue().size());
            // the object number of this object's parent; 
            obj.setParent(readInt(is));
            if (obj.getParent() != -1)
                parentIndex.put(obj.getOID(), obj.getParent());
            System.out.println("  parent="+obj.getParent());
            //  the object number of the first object in a linked list of this object's children (or -1 if it has no children);
            // the object number of the next object in a linked list of this object's parent's children (or -1 if the end of the list);
            readObj(is);
            readObj(is);
//            for (;;)
//            {
//                Integer c = readInt(is);
//                if (c == -1)
//                    break;
//                obj.getChildren().add(c);
//            }
//            System.out.println("  children size="+obj.getChildren().size());
            readObjVerbs(is, obj);
            readObjProps(store, is, obj, definedPropIndex, propValueIndex);
        }
        // stitch references
        for (int child : parentIndex.keySet())
        {
            int parent = parentIndex.get(child);
            MOOObject p = store.getCache().get(parent);
            p.getChildren().add(child);
        }
        for (int contained : locationIndex.keySet())
        {
            int container = locationIndex.get(contained);
            MOOObject c = store.getCache().get(container);
            addDefaultProperty(c, MOOObject.PROP_CONTENTS, new MOONumber(contained));
        }
        for (int oid : store.getCache().keySet())
            writeObjProps(store, oid, definedPropIndex, propValueIndex);
    }

    private static void setDefaultProperty(MOOObject obj, String name, MOOValue value)
    {
        MOOProperty prop = new MOOProperty();
        prop.setName(name);
        prop.setValue(value);
        obj.getProperties().put(prop.getName(), prop);
    }

    private static void addDefaultProperty(MOOObject obj, String name, MOOValue value)
    {
        MOOProperty prop = obj.getProperties().get(name);
        if (prop == null)
        {
            prop = new MOOProperty();
            prop.setName(name);
            prop.setValue(new MOOList());
            obj.getProperties().put(prop.getName(), prop);
        }
        ((MOOList)prop.getValue()).add(value);
    }
    
    private static void readObjProps(MemStore store, InputStream is, MOOObject obj, Map<Integer, List<MOOProperty>> definedPropIndex,
            Map<Integer, List<DeferredPropValue>> propValueIndex) throws IOException
    {
        // The next line contains the number of properties defined on this object, N. 
        int numDefinedProps = readInt(is);
        System.out.println("  defined props size="+numDefinedProps);
        // Following this are N lines of strings containing the name of each property. 
        List<MOOProperty> definedProps = new ArrayList<>();
        for (int j = 0; j < numDefinedProps; j++)
        {
            MOOProperty prop = new MOOProperty();
            prop.setName(readLine(is));
            prop.setDefinition(true);
            obj.getProperties().put(prop.getName(), prop);
            definedProps.add(prop);
            System.out.println("  prop #"+j+" "+prop.getName());
        }
        definedPropIndex.put(obj.getOID(), definedProps);
        // Finally, values for all of this object's properties are stored. 
        // The values are ordered sequentially, first giving values for each of the properties defined on this object, 
        // then values for each property defined on this object's parent, then for the parent's parent, 
        // and so on up to the top of the object's ancestry.
        //The first line contains the total number of property values which follow. For each property value: 
        int numPropVals = readInt(is);
        System.out.println("  prop values size="+numPropVals);
        List<DeferredPropValue> values = new ArrayList<>();
        for (int j = 0; j < numPropVals; j++)
        {   // For each property value:
            //System.out.println("  prop #"+j+" ");
            DeferredPropValue value = new DeferredPropValue();
            // a number indicating the typeof() of the datatype, or the number 5 to represent a clear value; 
            value.val = readValue(is);
            // the object number of the owner of the property (on this object); 
            value.owner = readObj(is);
            // a number containing permissions flags, described below [3]; 
            value.flags = readInt(is);
            values.add(value);
        }
        propValueIndex.put(obj.getOID(), values);
    }
    
    private static void writeObjProps(MemStore store, int oid, Map<Integer, List<MOOProperty>> definedPropIndex,
            Map<Integer, List<DeferredPropValue>> propValueIndex)
    {
        MOOObject obj = store.getCache().get(oid);
        List<DeferredPropValue> values = propValueIndex.get(oid);

        int propsObj = obj.getOID();
        List<MOOProperty> propsQueue = new ArrayList<>();
        propsQueue.addAll(definedPropIndex.get(propsObj));
        //The first line contains the total number of property values which follow. For each property value: 
        int numPropVals = values.size();
        int numDefinedProps = definedPropIndex.get(oid).size();
        //System.out.println("  prop values size="+numPropVals);
        for (int j = 0; j < numPropVals; j++)
        {   // For each property value:
            while (propsQueue.size() <= j)
            {
                propsObj = store.getCache().get(propsObj).getParent();
                propsQueue.addAll(definedPropIndex.get(propsObj));
            }
            MOOProperty prop = propsQueue.get(j);
            //System.out.println("  prop #"+j+" "+prop.getName());
            if (j >= numDefinedProps)
            {
                prop = new MOOProperty(prop);
                obj.getProperties().put(prop.getName(), prop);
            }
            DeferredPropValue value = values.get(j);
            // a number indicating the typeof() of the datatype, or the number 5 to represent a clear value; 
            if (value.val == null)
                prop.setClear(true);
            else
                prop.setValue(value.val);
            // the object number of the owner of the property (on this object); 
            prop.setOwner(value.owner);
            // a number containing permissions flags, described below [3]; 
            int op = value.flags;
            if ((op&OP_read) != 0)
                prop.setRead(true);
            if ((op&OP_write) != 0)
                prop.setWrite(true);
            if ((op&OP_chown) != 0)
                prop.setChange(true);
        }
        
    }

    public static void readObjVerbs(BufferedInputStream is, 
            MOOObject obj) throws IOException
    {
        // the number of verbs defined on this object, regardless of whether they have been programmed; 
        int numVerbs = readInt(is);
        System.out.println("  verbs size="+numVerbs);
        for (int j = 0; j < numVerbs; j++)
        {   // For each verb: 
            MOOVerb verb = new MOOVerb();
            // a string containing the name(s) for the verb; 
            verb.setName(readLine(is));
            System.out.println("  verb #"+j+" "+verb.getName());
            // the object number of the owner of the verb; 
            verb.setOwner(readObj(is));
            // a number containing permissions flags, described below [2];
            int vp = readInt(is);
            if ((vp&VP_read) != 0)
                verb.setRead(true);
            if ((vp&VP_write) != 0)
                verb.setWrite(true);
            if ((vp&VP_execute) != 0)
                verb.setExecute(true);
            if ((vp&VP_dobj) == VP_dobj_none)
                verb.setDirectObjectType(MOOVerb.DO_NONE);
            if ((vp&VP_dobj) == VP_dobj_any)
                verb.setDirectObjectType(MOOVerb.DO_ANY);
            if ((vp&VP_dobj) == VP_dobj_this)
                verb.setDirectObjectType(MOOVerb.DO_THIS);
            if ((vp&VP_iobj) == VP_iobj_none)
                verb.setIndirectObjectType(MOOVerb.IO_NONE);
            if ((vp&VP_iobj) == VP_iobj_any)
                verb.setIndirectObjectType(MOOVerb.IO_ANY);
            if ((vp&VP_iobj) == VP_iobj_this)
                verb.setIndirectObjectType(MOOVerb.IO_THIS);
            // a number containing an argument preposition code; 
            verb.setPrepositionType(readInt(is));
            obj.getVerbs().add(verb);
        }
    }

    private static MOOValue readValue(InputStream is) throws IOException
    {
        int typeof = readInt(is);
        if (typeof == 5)
            return null;
        else if (typeof == 0) // int
            return new MOONumber(readInt(is));
        else if (typeof == 1) // obj
            return new MOOObjRef(readInt(is));
        else if (typeof == 2) // str
            return new MOOString(readLine(is));
        else if (typeof == 3) // err
            return new MOONumber(readInt(is));
        else if (typeof == 4) // list
        {
            int size = readInt(is);
            MOOList ret = new MOOList();
            for (int i = 0; i < size; i++)
                ret.add(readValue(is));
            return ret;
        }
        else if (typeof == 9)
        {
            MOONumber ret = new MOONumber();
            ret.setValue(DoubleUtils.parseDouble(readLine(is)));
            return ret;
        }
        throw new IllegalArgumentException("Unknown typeof="+typeof+", line="+readLine(is));
    }
    
    private static void readPlayers(MemStore store, BufferedInputStream is, int totalPlayers) throws IOException
    {
        // The next N lines comprise a list of player object numbers. N is equal to the value of line 4 in the db header. 
        for (int i = 0; i < totalPlayers; i++)
        {
            String inbuf = readLine(is);
            int player = IntegerUtils.parseInt(inbuf);
            store.getPlayerList().add(player);
        }
    }

    public static int[] readHeaders(MemStore store, BufferedInputStream is)
            throws IOException
    {
        //The first four lines comprise a header of:
        //the total number of objects in the database, including recycled objects (also the value of max_object() plus one);
        String firstLine = readLine(is);
        if (firstLine.startsWith("** "))
            firstLine = readLine(is);
        store.setMaxObject(IntegerUtils.parseInt(firstLine) - 1);
        // the total number of verb programs in the database, not including unprogrammed verbs;
        String secondLine = readLine(is);
        int totalVerbs = IntegerUtils.parseInt(secondLine);
        // a number whose value is uninteresting, and
        String thirdLine = readLine(is);
        @SuppressWarnings("unused")
        int uninteresting = IntegerUtils.parseInt(thirdLine);
        // the total number of objects whose player flag is set. 
        String fourthLine = readLine(is);
        int totalPlayers = IntegerUtils.parseInt(fourthLine);
        return new int[] { totalVerbs, totalPlayers};
    }
    
    private static MOOObjRef readObj(InputStream is) throws IOException
    {
        return new MOOObjRef(readInt(is));
    }
    
    private static int readInt(InputStream is) throws IOException
    {
        return IntegerUtils.parseInt(readLine(is));
    }
    
    private static String readLine(InputStream is) throws IOException
    {
        StringBuffer line = new StringBuffer();
        for (;;)
        {
            int ch = is.read();
            if (ch == -1)
            {
                if (line.length() == 0)
                    throw new IOException("Unexpected end of file");
                break;
            }
            if (ch == '\r')
                continue;
            if (ch == '\n')
                break;
            line.append((char)ch);
        }
        //System.out.println(">"+line);
        return line.toString();
    }
    
    public static void main(String[] argv)
    {
        MemStore store = new MemStore();
        //File disk = new File("D:\\Documents\\ws\\echo\\com.tsatsatzu.moo.core\\dbs\\minimal.db");
        //File disk = new File("D:\\Documents\\ws\\echo\\com.tsatsatzu.moo.core\\dbs\\LambdaCore-latest.db");
        File disk = new File("D:\\Documents\\ws\\echo\\com.tsatsatzu.moo.core\\dbs\\LambdaCore-20Jun18.db");
        //File disk = new File("D:\\Documents\\ws\\echo\\com.tsatsatzu.moo.core\\dbs\\JHCore-03Nov18.db");
        try
        {
            importFromDisk(store, disk);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class DeferredPropValue
{
    MOOValue val;
    MOOObjRef owner;
    int flags;
}