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
    
    //Assert.assertTrue(classes.contains("com.kas.infra.base.AKasObject"));   // a class from this project
    //Assert.assertTrue(classes.contains("java.lang.String"));                // a java-base class
    Assert.assertTrue(classes.contains("org.apache.logging.log4j.Logger")); // 3rd party
  }
  
  @Test
  public void testGetUrlClasses()
  {
    Set<String> classes = mClassPath.getUrlClasses("d:/dev/src/java/kas/kas-infra/bin/main");
    
    Assert.assertTrue(classes.contains("com.kas.infra.base.AKasObject"));
  }
}
