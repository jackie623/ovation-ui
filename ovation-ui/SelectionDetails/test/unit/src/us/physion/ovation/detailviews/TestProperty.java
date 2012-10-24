/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

/**
 *
 * @author huecotanks
 */
class TestProperty {
    String key;
    Object value;
    TestProperty(String key, Object value)
    {
        this.key = key;
        this.value = value;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public Object getValue()
    {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj instanceof TestProperty)
        {
            return (((TestProperty)obj).getKey().equals(key) && ((TestProperty)obj).getValue().equals(value));
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return key.hashCode();
    }
}
