package com.kas.infra.classes;

import java.util.Set;
import org.junit.Test;
import junit.framework.Assert;

public class TestClassPath
{
  private ClassPath mClassPath = new ClassPath();
  
  @Test
  public void testGetClasses()
  {
    Set<String> classes = mClassPath.getClasses();
    Assert.assertTrue(classes.contains("com.kas.infra.base.AKasObject"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Logger"));
  }
  
  @Test
  public void testGetUrlClasses()
  {
    Set<String> classes = mClassPath.getUrlClasses("./bin/main");
    Assert.assertTrue(classes.contains("com.kas.infra.base.AKasObject"));
    Assert.assertTrue(classes.contains("com.kas.infra.base.ProductVersion"));
    Assert.assertFalse(classes.contains(this.getClass().getName()));
  }
  
  @Test
  public void testGetPackageClasses()
  {
    Set<String> classes = null;
    classes = mClassPath.getPackageClasses("org.apache.logging.log4j");
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.EventLogger"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Level"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Logger"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.LogManager"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.core.Logger"));
    Assert.assertFalse(classes.contains("org.apache.logging.log4j.ClassToFailSearch"));
    
    classes = mClassPath.getPackageClasses("org.apache.logging.log4j", false);
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.EventLogger"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Level"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Logger"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.LogManager"));
    Assert.assertFalse(classes.contains("org.apache.logging.log4j.core.Logger"));
    Assert.assertFalse(classes.contains("org.apache.logging.log4j.ClassToFailSearch"));
  }
}
