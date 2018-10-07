package junit.kas.infra.base;

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
  public void testResolveNullProps()
  {
    
  }
  
  @Test
  public void testResolveWithProps()
  {
    
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
    RunTimeUtils.setProperty("shy.name", "shy", true);
    
    Class<?> [] pClasses = { String.class , Properties.class };
    Method getVarValue = PropertyResolver.class.getDeclaredMethod("getVarValue", pClasses);
    boolean access = getVarValue.isAccessible();
    getVarValue.setAccessible(true);
    
    Object [] pList1 = { "${shy.name}" , props };
    Object [] pList2 = { "${shy.name}" , null };
    Assert.assertEquals(getVarValue.invoke(null, pList1), "shy");
    Assert.assertEquals(getVarValue.invoke(null, pList2), "shy");
    
    getVarValue.setAccessible(access);
  }
}
