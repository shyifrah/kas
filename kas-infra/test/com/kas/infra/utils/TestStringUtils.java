package com.kas.infra.utils;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.Counter;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

public class TestStringUtils
{
  @Test
  public void testObjectAsString()
  {
    Object obj = null;
    Object counter = new Counter("a");
    
    Assert.assertEquals( StringUtils.asString(obj), "null");
    Assert.assertEquals( StringUtils.asString(counter), "a=[0]");
    
    String key, val;
    key = null; val = null;
    Assert.assertEquals( StringUtils.asString(key, val), "null=null");
    key = "shy"; val = null;
    Assert.assertEquals( StringUtils.asString(key, val), "shy=null");
    key = null; val = "shy";
    Assert.assertEquals( StringUtils.asString(key, val), "null=shy");
    key = "pippo"; val = "shy";
    Assert.assertEquals( StringUtils.asString(key, val), "pippo=shy");
  }
  
  @Test
  public void testIObjectAsPrintableString()
  {
    IObject iObj = null;
    IObject counter = new Counter("a");
    
    Assert.assertEquals( StringUtils.asPrintableString(iObj), "null");
    Assert.assertEquals( StringUtils.asPrintableString(counter), "a=[0]");
  }
  
  @Test
  public void testThrowableFormatter()
  {
    Exception e = new Exception("Debug exception");
    String pref = e.getClass().getName() + ':';
    
    Assert.assertTrue( StringUtils.format(e).startsWith(pref) );
  }
  
  @Test
  public void testGetPadding()
  {
    int level;
    
    level = 0;
    Assert.assertEquals( StringUtils.getPadding(level), "" );
    level = 1;
    Assert.assertEquals( StringUtils.getPadding(level), "  " );
    level = 5;
    Assert.assertEquals( StringUtils.getPadding(level), "          " );
  }
  
  @Test
  public void testDuplicate()
  {
    int num;
    String str;
    
    num = 0; str = "";
    Assert.assertEquals( StringUtils.duplicate(str, num), "" );
    num = 1; str = "";
    Assert.assertEquals( StringUtils.duplicate(str, num), "" );
    num = 4; str = "";
    Assert.assertEquals( StringUtils.duplicate(str, num), "" );
    
    num = 0; str = "x";
    Assert.assertEquals( StringUtils.duplicate(str, num), "" );
    num = 1; str = "x";
    Assert.assertEquals( StringUtils.duplicate(str, num), "x" );
    num = 4; str = "x";
    Assert.assertEquals( StringUtils.duplicate(str, num), "xxxx" );
    
    num = 0; str = "###";
    Assert.assertEquals( StringUtils.duplicate(str, num), "" );
    num = 1; str = "###";
    Assert.assertEquals( StringUtils.duplicate(str, num), "###" );
    num = 4; str = "###";
    Assert.assertEquals( StringUtils.duplicate(str, num), "############" );
  }
  
  @Test
  public void testTitle()
  {
    String str = "shy";
    String title = "===================\n" +
                   "===     shy     ===\n" +
                   "===================";
    
    Assert.assertEquals( StringUtils.title(str), title );
  }
  
  @Test
  public void testTruncate()
  {
    String str = "shyifrah";
    
    Assert.assertEquals( StringUtils.trunc(str, 5, '#'), "shyif" );
    Assert.assertEquals( StringUtils.trunc(str, 10, '#'), "shyifrah##" );
    Assert.assertEquals( StringUtils.trunc(str, 5), "shyif" );
    Assert.assertEquals( StringUtils.trunc(str, 10), "shyifrah  " );
  }
  
  @Test
  public void testAsHexString()
  {
    byte [] bytes = { 0x0d, 0x0e , 0x0a, 0x0d, 0x0c, 0x00, 0x0d, 0x0e };
    Assert.assertEquals( StringUtils.asHexString(bytes), "0D0E0A0D0C000D0E" );
  }
}
