package com.kas.infra.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyResolver;
import com.kas.infra.utils.RunTimeUtils;

public class TestPropertyResolver
{
  @Test
  public void testResolve()
  {
    Properties props = new Properties();
    props.setStringProperty("kas.home", "/usr/local/kas");
    props.setStringProperty("kas.user", "pippo");
    props.setStringProperty("kas.mq.mgr", "alpha");
    
    Assert.assertEquals(  PropertyResolver.resolve("${kas.home", props)              , "${kas.home"           );
    Assert.assertEquals(  PropertyResolver.resolve("${kas.home}", props)             , "/usr/local/kas"       );
    Assert.assertEquals(  PropertyResolver.resolve("${kas.home}/${kas.user}", props) , "/usr/local/kas/pippo" );
    Assert.assertEquals(  PropertyResolver.resolve("${kas.home.user.name}", props)   , ""                     );
    Assert.assertEquals(  PropertyResolver.resolve("/pippo/${kas.user}/", props)     , "/pippo/pippo/"        );
  }
  
  @Test
  public void testStrip() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
  {
    String [] vars = { "${}" , "${a}", "${abc}" };
    String [] exrs = { ""    , "a"   , "abc"    };
    
    Class<?> [] pClasses = { String.class };
    Method strip = PropertyResolver.class.getDeclaredMethod("strip", pClasses);
    boolean access = strip.isAccessible();
    strip.setAccessible(true);
    
    for (int i = 0; i < vars.length; ++i)
    {
      String str = vars[i];
      String exr = exrs[i];
      Assert.assertEquals( strip.invoke(null, str) , exr);
    }
    
    strip.setAccessible(access);
  }
  
  @Test
  public void testGetVarValue() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    Properties props = new Properties();
    props.setStringProperty("shy.name", "pippo");
    
    Class<?> [] pClasses = { String.class , Properties.class };
    Method getVarValue = PropertyResolver.class.getDeclaredMethod("getVarValue", pClasses);
    boolean access = getVarValue.isAccessible();
    getVarValue.setAccessible(true);
    
    RunTimeUtils.setProperty("shy.name", "shy", true);
    Object [] pList1 = { "${shy.name}" , props };
    Assert.assertEquals(getVarValue.invoke(null, pList1), "shy");
    
    RunTimeUtils.setProperty("shy.name", "", true);
    Object [] pList2 = { "${shy.name}" , props };
    Assert.assertEquals(getVarValue.invoke(null, pList2), "pippo");
    
    getVarValue.setAccessible(access);
  }
}
