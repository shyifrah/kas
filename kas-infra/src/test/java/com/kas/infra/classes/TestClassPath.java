package com.kas.infra.classes;

import java.util.Map;
import org.junit.Test;
import junit.framework.Assert;

public class TestClassPath
{
  private ClassPath mClassPath = new ClassPath();
  
  @Test
  public void testGetClasses()
  {
    Map<String, Class<?>> classes = mClassPath.getClasses();
    Assert.assertTrue ( classes.containsKey("com.kas.infra.base.AKasObject")   );
    Assert.assertTrue ( classes.containsKey("org.apache.logging.log4j.Logger") );
  }
  
  @Test
  public void testGetUrlClasses()
  {
    Map<String, Class<?>> classes = mClassPath.getUrlClasses("./bin/main");
    Assert.assertTrue  ( classes.containsKey("com.kas.infra.base.AKasObject")     );
    Assert.assertTrue  ( classes.containsKey("com.kas.infra.base.ProductVersion") );
    Assert.assertFalse ( classes.containsKey(this.getClass().getName())           );
  }
  
  @Test
  public void testGetPackageClasses()
  {
    Map<String, Class<?>> classes = null;
    classes = mClassPath.getPackageClasses("org.apache.logging.log4j");
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.EventLogger")       );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.Level")             );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.Logger")            );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.LogManager")        );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.core.Logger")       );
    Assert.assertFalse ( classes.containsKey("org.apache.logging.log4j.ClassToFailSearch") );
    
    classes = mClassPath.getPackageClasses("org.apache.logging.log4j", false);
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.EventLogger")       );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.Level")             );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.Logger")            );
    Assert.assertTrue  ( classes.containsKey("org.apache.logging.log4j.LogManager")        );
    Assert.assertFalse ( classes.containsKey("org.apache.logging.log4j.core.Logger")       );
    Assert.assertFalse ( classes.containsKey("org.apache.logging.log4j.ClassToFailSearch") );
  }
}
