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
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Logger"));
  }
  
  @Test
  public void testGetUrlClasses()
  {
    Set<String> classes = mClassPath.getUrlClasses("d:/dev/src/java/kas/kas-infra/bin/main");
    Assert.assertTrue(classes.contains("com.kas.infra.base.AKasObject"));
  }
  
  @Test
  public void testGetPackageClasses()
  {
    Set<String> classes = mClassPath.getPackageClasses("org.apache.logging.log4j");
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.CloseableThreadContext"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.EventLogger"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Level"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Logger"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.LoggingException"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.LogManager"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Marker"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.MarkerManager"));
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.ThreadContext"));
    Assert.assertEquals(9, classes.size());
  }
}
