package junit.kas.infra.base;

import org.junit.Assert;
import org.junit.Test;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;

public class TestProperties
{
  private Properties mProps = new Properties();
  
  @Test
  public void testProperties()
  {
    boolean bool = true;
    byte by = 3;
    byte [] bytes = "shyush.pippo.ifrah".getBytes();
    char ch = 'x';
    double dbl = 0.002;
    float flt = (float) 1.2;
    int n = 9;
    long l = 123456789000L;
    Object obj = new String("lalakers");
    short sht = 320;
    String str = "SHY";
    
    mProps.setBoolProperty   ( "key.bool"    , bool    );
    mProps.setByteProperty   ( "key.byte"    , by      );
    mProps.setBytesProperty  ( "key.byte[]"  , bytes   );
    mProps.setBytesProperty  ( "key.byte[]2" , bytes , 7, 5);
    mProps.setCharProperty   ( "key.char"    , ch      );
    mProps.setDoubleProperty ( "key.double"  , dbl     );
    mProps.setFloatProperty  ( "key.float"   , flt     );
    mProps.setIntProperty    ( "key.int"     , n       );
    mProps.setLongProperty   ( "key.long"    , l       );
    mProps.setObjectProperty ( "key.object"  , obj     );
    mProps.setShortProperty  ( "key.short"   , sht     );
    mProps.setStringProperty ( "key.string"  , str     );
    
    Assert.assertEquals(mProps.size(), 12);
    Assert.assertTrue(mProps.containsKey("key.bool"));
    Assert.assertFalse(mProps.isEmpty());
    Assert.assertEquals(mProps.name(), "<Properties>");
  }
  
  @Test
  public void testGetBoolProperty()
  {
    String key = "key.bool";
    boolean exr = true;
    String msgpref = "getBoolProperty()";
    try
    {
      Assert.assertEquals(mProps.getBoolProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetByteProperty()
  {
    String key = "key.byte";
    byte exr = 3;
    String msgpref = "getByteProperty()";
    try
    {
      Assert.assertEquals(mProps.getByteProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetBytesProperty()
  {
    String key = "key.byte[]";
    byte [] exr = "shyush.pippo.ifrah".getBytes();
    String msgpref = "getBytesProperty()";
    try
    {
      Assert.assertEquals(mProps.getBytesProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetBytesProperty2()
  {
    String key = "key.byte[]2";
    byte [] exr = "pippo".getBytes();
    String msgpref = "getBytesProperty()";
    try
    {
      Assert.assertEquals(mProps.getBytesProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetCharProperty()
  {
    String key = "key.char";
    char exr = 'x';
    String msgpref = "getCharProperty()";
    try
    {
      Assert.assertEquals(mProps.getCharProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetDoubleProperty()
  {
    String key = "key.double";
    double exr = 0.002;
    String msgpref = "getDoubleProperty()";
    try
    {
      Assert.assertTrue(mProps.getDoubleProperty(key) == exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetFloatProperty()
  {
    String key = "key.float";
    float exr = (float) 1.2;
    String msgpref = "getFloatProperty()";
    try
    {
      Assert.assertTrue(mProps.getFloatProperty(key) == exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetIntProperty()
  {
    String key = "key.int";
    int exr = 9;
    String msgpref = "getIntProperty()";
    try
    {
      Assert.assertTrue(mProps.getIntProperty(key) == exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetLongProperty()
  {
    String key = "key.long";
    long exr = 123456789000L;
    String msgpref = "getLongProperty()";
    try
    {
      Assert.assertTrue(mProps.getLongProperty(key) == exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetObjectProperty()
  {
    String key = "key.object";
    Object exr = new String("lalakers");
    String msgpref = "getObjectProperty()";
    try
    {
      Assert.assertEquals(mProps.getObjectProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetShortProperty()
  {
    String key = "key.short";
    short exr = 320;
    String msgpref = "getShortProperty()";
    try
    {
      Assert.assertEquals(mProps.getShortProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
  
  @Test
  public void testGetStringProperty()
  {
    String key = "key.string";
    String exr = "SHY";
    String msgpref = "getStringProperty()";
    try
    {
      Assert.assertEquals(mProps.getStringProperty(key), exr);
    }
    catch (KasException e)
    {
      Assert.assertTrue(e.getMessage().startsWith(msgpref));
    }
  }
}
