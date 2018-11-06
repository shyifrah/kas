package com.kas.infra.base;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.ProductVersion;

public class TestProductVersion
{
  private ProductVersion mVersion = new ProductVersion(1, 2, 3, 4);
  
  @Test
  public void testToString()
  {
    Assert.assertEquals(mVersion.toString(), "1-2-3-4");
  }
  
  @Test
  public void testGetMajor()
  {
    Assert.assertEquals(mVersion.getMajorVersion(), 1);
  }
  
  @Test
  public void testGetMinor()
  {
    Assert.assertEquals(mVersion.getMinorVersion(), 2);
  }
  
  @Test
  public void testGetMod()
  {
    Assert.assertEquals(mVersion.getModification(), 3);
  }
  
  @Test
  public void testGetBuild()
  {
    Assert.assertEquals(mVersion.getBuildNumber(), 4);
  }
}
