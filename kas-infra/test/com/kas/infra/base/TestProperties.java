package com.kas.infra.base;

import com.kas.infra.test.ObjectTest;

public class TestProperties
{
  static public void main(String [] args)
  {
    long time = System.currentTimeMillis();
    ObjectTest oTest;
    Properties props = new Properties();
    
    // currently it is impossible to compare two objects
    // of type byte [] as the current infrastructure just
    // does a x.equals(y) which isn't sufficient for byte []. 
    
    props.setBoolProperty("kas.test.boolean", true);
    props.setByteProperty("kas.test.byte", (byte)0x01);
    //props.setBytesProperty("kas.test.bytes", "kas.test.bytes".getBytes());
    //props.setBytesProperty("kas.test.sub.bytes", "kas.test.sub.bytes".getBytes(), 4, 4);
    props.setCharProperty("kas.test.char", 'X');
    props.setFloatProperty("kas.test.float", (float)1.01);
    props.setIntProperty("kas.test.int", 3);
    props.setLongProperty("kas.test.long", time);
    props.setObjectProperty("kas.test.object", new Integer(10));
    props.setShortProperty("kas.test.short", (short)45);
    props.setStringProperty("kas.test.string", "Shy Ifrah");
    
    
    oTest = new ObjectTest(props);
    oTest.add("getBoolProperty", true, "kas.test.boolean", false);
    oTest.add("getByteProperty", (byte)0x01, "kas.test.byte", (byte)0x00);
    //oTest.add("getBytesProperty", "kas.test.bytes".getBytes(), "kas.test.bytes", new byte [4]);
    //oTest.add("getBytesProperty", "test".getBytes(), "kas.test.sub.bytes", new byte [4]);
    oTest.add("getCharProperty", 'X', "kas.test.char", 'C');
    oTest.add("getFloatProperty", (float)1.01, "kas.test.float", (float)0.0);
    oTest.add("getIntProperty", 3, "kas.test.int", 1);
    oTest.add("getLongProperty", time, "kas.test.long", 0L);
    oTest.add("getObjectProperty", new Integer(10), "kas.test.object", null);
    oTest.add("getShortProperty", (short)45, "kas.test.short", (short)0);
    oTest.add("getStringProperty", "Shy Ifrah", "kas.test.string", "kuku");
    oTest.add("size", 9);
    oTest.add("isEmpty", false);
    
    oTest.run();
  }
}
