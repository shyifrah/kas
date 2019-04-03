package com.kas.infra.classes;

import java.util.Map;
import org.junit.Test;
import junit.framework.Assert;

public class TestClassUtils
{
  @Test
  public void testGetDirClasses()
  {
    String dir = "./bin/main";
    Map<String, Class<?>> map = ClassUtils.getDirClasses(dir);
    
    Assert.assertTrue(map.containsKey("com.kas.infra.base.AKasObject"));
  }
  
  @Test
  public void testGetJarClasses()
  {
    String jar = "./build/libs/kas-infra-1.0.0.jar";
    Map<String, Class<?>> map = ClassUtils.getJarClasses(jar);
    
    Assert.assertTrue(map.containsKey("com.kas.infra.base.AKasObject"));
  }
}
