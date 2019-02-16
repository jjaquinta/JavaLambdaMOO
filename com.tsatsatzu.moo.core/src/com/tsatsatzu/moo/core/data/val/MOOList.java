package com.tsatsatzu.moo.core.data.val;

import java.util.ArrayList;
import java.util.List;

import com.tsatsatzu.moo.core.data.MOOValue;

public class MOOList extends MOOValue
{
    private List<MOOValue> mValue = new ArrayList<MOOValue>();
    
    // constructors
    public MOOList()
    {        
    }
    
    // utilities
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < mValue.size(); i++)
        {
            if (i > 0)
                sb.append(",");
            sb.append(" ");
            sb.append(mValue.get(i).toString());
        }
        sb.append(" ]");
        return sb.toString();
    }
    
    // getters and setters

    public List<MOOValue> getValue()
    {
        return mValue;
    }

    public void setValue(List<MOOValue> value)
    {
        mValue = value;
    }
}
