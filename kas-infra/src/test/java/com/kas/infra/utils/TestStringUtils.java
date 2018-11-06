package com.kas.infra.utils;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;

public class TestStringUtils
{
  @Test
  public void testObjectAsString()
  {
    Object obj = null;
    Object str = new String("a");
    
    Assert.assertEquals( StringUtils.asString(obj), "null");
    Assert.assertEquals( StringUtils.asString(str), "a");
    
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
    IObject uuid = UniqueId.fromByteArray( new byte [] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F } );
    
    Assert.assertEquals( StringUtils.asPrintableString(iObj), "null");
    Assert.assertEquals( StringUtils.asPrintableString(uuid), "01020304-0506-0708-0900-0a0b0c0d0e0f");
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
  
  @Test
  public void testClearBlanks()
  {
    String str01 = null;        // return null
    String str02 = "";          // return ""
    String str03 = " ";         // return " ";
    String str04 = "  ";        // return " ";
    String str05 = "   ";       // return " ";
    String str06 = "a";         // return "a";
    String str07 = "aa";        // return "aa";
    String str08 = "a a";       // return "a a";
    String str09 = "a  a";      // return "a a";
    String str10 = "aa a";      // return "aa a";
    String str11 = "a aa";      // return "a aa";
    String str12 = "a a a";     // return "a a a";
    String str13 = "aa  a";     // return "aa a";
    String str14 = "a  aa";     // return "a aa";
    String str15 = "a  a a";    // return "a a a";
    String str16 = "a  a  a";   // return "a a a";
    String str17 = "aa aa";     // return "aa aa";
    String str18 = "aa  aa";    // return "aa aa";
    String str19 = "aa     aa"; // return "aa aa";
    String str20 = "aaaa ";     // return "aaaa ";
    String str21 = "aaaa  ";    // return "aaaa ";
    String str22 = " aaaa";     // return " aaaa";
    String str23 = "  aaaa";    // return " aaaa";
    String str24 = " aaaa ";    // return " aaaa ";
    String str25 = "  aaaa  ";  // return " aaaa ";
    
    Assert.assertEquals( StringUtils.clearBlanks(str01), null );
    Assert.assertEquals( StringUtils.clearBlanks(str02), "" );
    Assert.assertEquals( StringUtils.clearBlanks(str03), " " );
    Assert.assertEquals( StringUtils.clearBlanks(str04), " " );
    Assert.assertEquals( StringUtils.clearBlanks(str05), " " );
    Assert.assertEquals( StringUtils.clearBlanks(str06), "a" );
    Assert.assertEquals( StringUtils.clearBlanks(str07), "aa" );
    Assert.assertEquals( StringUtils.clearBlanks(str08), "a a" );
    Assert.assertEquals( StringUtils.clearBlanks(str09), "a a" );
    Assert.assertEquals( StringUtils.clearBlanks(str10), "aa a" );
    Assert.assertEquals( StringUtils.clearBlanks(str11), "a aa" );
    Assert.assertEquals( StringUtils.clearBlanks(str12), "a a a" );
    Assert.assertEquals( StringUtils.clearBlanks(str13), "aa a" );
    Assert.assertEquals( StringUtils.clearBlanks(str14), "a aa" );
    Assert.assertEquals( StringUtils.clearBlanks(str15), "a a a" );
    Assert.assertEquals( StringUtils.clearBlanks(str16), "a a a" );
    Assert.assertEquals( StringUtils.clearBlanks(str17), "aa aa" );
    Assert.assertEquals( StringUtils.clearBlanks(str18), "aa aa" );
    Assert.assertEquals( StringUtils.clearBlanks(str19), "aa aa" );
    Assert.assertEquals( StringUtils.clearBlanks(str20), "aaaa " );
    Assert.assertEquals( StringUtils.clearBlanks(str21), "aaaa " );
    Assert.assertEquals( StringUtils.clearBlanks(str22), " aaaa" );
    Assert.assertEquals( StringUtils.clearBlanks(str23), " aaaa" );
    Assert.assertEquals( StringUtils.clearBlanks(str24), " aaaa " );
    Assert.assertEquals( StringUtils.clearBlanks(str25), " aaaa " );
  }
}
