package com.kas.infra.base;

import org.junit.Test;
import junit.framework.Assert;

public class TestAKasObject extends AKasObject
{
  @Test
  public void testToString()
  {
    Assert.assertEquals( name() , toString() );
  }
  
  @Test
  public void testName()
  {
    Assert.assertEquals( "<" + TestAKasObject.class.getSimpleName() + ">" , name() );
  }
  
  @Test
  public void testToPrintable()
  {
    Assert.assertEquals( "SHY" , toPrintableString() );
    Assert.assertEquals( "SHY" , toPrintableString(0) );
    Assert.assertEquals( "  SHY" , toPrintableString(1) );
    Assert.assertEquals( "    SHY" , toPrintableString(2) );
  }
  
  public String toPrintableString(int level)
  {
    return pad(level) + "SHY";
  }
}
