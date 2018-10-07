package com.kas.infra.base;

import com.kas.infra.test.ObjectTest;

public class TestProductVersion
{
  static public void main(String [] args)
  {
    
    ObjectTest oTest;
    
    ProductVersion pv = new ProductVersion(1, 2, 3, 4);
    
    oTest = new ObjectTest(pv);
    oTest.add("getMajorVersion", 1);
    oTest.add("getMinorVersion", 2);
    oTest.add("getModification", 3);
    oTest.add("getBuildNumber", 4);
    oTest.add("toString", "1-2-3-4");
    
    oTest.run();
  }
}
