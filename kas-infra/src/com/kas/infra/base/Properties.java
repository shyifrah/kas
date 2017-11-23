package com.kas.infra.base;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.config.IConfiguration;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.StringUtils;

public class Properties extends ConcurrentHashMap<Object, Object> implements IConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public  static final String cIncludeKey      = "kas.include";
  private static final long   serialVersionUID = 1L;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Properties()
  {
    super();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Object getProperty(Object key)
  {
    if (key == null)
      return null;
    
    return super.get(key);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean getBoolProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getBoolProperty() - Property not found: " + key);
    
    Boolean result;
    try
    {
      result = (Boolean)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Boolean.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getBoolProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    boolean value = defaultValue;
    try
    {
      value = getBoolProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setBoolProperty(String key, boolean value)
  {
    put(key, new Boolean(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getIntProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getIntProperty() - Property not found: " + key);
    
    Integer result;
    try
    {
      result = (Integer)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Integer.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getIntProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getIntProperty(String key, int defaultValue)
  {
    int value = defaultValue;
    try
    {
      value = getIntProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setIntProperty(String key, int value)
  {
    put(key, new Integer(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getStringProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getStringProperty() - Property not found: " + key);
    
    String result;
    try
    {
      result = (String)objResult;
    }
    catch (ClassCastException e)
    {
      result = objResult.toString();
    }
    catch (Throwable e)
    {
      throw new KasException("getStringProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getStringProperty(String key, String defaultValue)
  {
    String value = defaultValue;
    try
    {
      value = getStringProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setStringProperty(String key, String value)
  {
    put(key, new String(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public long getLongProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getLongProperty() - Property not found: " + key);
    
    Long result;
    try
    {
      result = (Long)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Long.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getLongProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public long getLongProperty(String key, long defaultValue)
  {
    long value = defaultValue;
    try
    {
      value = getLongProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setLongProperty(String key, long value)
  {
    put(key, new Long(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public byte getByteProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getByteProperty() - Property not found: " + key);
    
    Byte result;
    try
    {
      result = (Byte)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Byte.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getByteProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public byte getByteProperty(String key, byte defaultValue)
  {
    byte value = defaultValue;
    try
    {
      value = getByteProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setByteProperty(String key, byte value)
  {
    put(key, new Byte(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public double getDoubleProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getDoubleProperty() - Property not found: " + key);
    
    Double result;
    try
    {
      result = (Double)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Double.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getDoubleProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public double getDoubleProperty(String key, double defaultValue)
  {
    double value = defaultValue;
    try
    {
      value = getLongProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setDoubleProperty(String key, double value)
  {
    put(key, new Double(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public float getFloatProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getFloatProperty() - Property not found: " + key);
    
    Float result;
    try
    {
      result = (Float)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Float.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getFloatProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public float getFloatProperty(String key, float defaultValue)
  {
    float value = defaultValue;
    try
    {
      value = getFloatProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setFloatProperty(String key, float value)
  {
    put(key, new Float(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Object getObjectProperty(String key) throws KasException
  {
    Object result = getProperty(key);
    if (result == null)
      throw new KasException("getObjectProperty() - Property not found: " + key);
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Object getObjectProperty(String key, Object defaultValue)
  {
    Object value = defaultValue;
    try
    {
      value = getObjectProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setObjectProperty(String key, Object value)
  {
    put(key, value);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public short getShortProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getShortProperty() - Property not found: " + key);
    
    Short result;
    try
    {
      result = (Short)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      result = Short.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getShortProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public short getShortProperty(String key, short defaultValue)
  {
    short value = defaultValue;
    try
    {
      value = getShortProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void setShortProperty(String key, short value)
  {
    put(key, new Short(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Properties getSubset(String keyPrefix)
  {
    if (keyPrefix == null)
      return null;
    
    Properties subset = new Properties();
    subset.putAll(this);
    for (Map.Entry<Object, Object> entry : super.entrySet())
    {
      String key = (String)entry.getKey();
      if (!key.startsWith(keyPrefix))
        subset.remove(key);
    }
    
    return subset;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void load(String fileName)
  {
    load(fileName, this);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void load(String fileName, Properties properties)
  {
    // now try to load the properties inside this file
    File file = new File(fileName);
    List<String> input = null;
    if ((file.exists()) && (file.isFile()) && (file.canRead()))
    {
      input = FileUtils.load(file);
      
      for (int i = 0; i < input.size(); ++i)
      {
        String [] parsedLine = input.get(i).split("=");
        
        if (parsedLine.length == 2)
        {
          String key = parsedLine[0].trim();
          String val = parsedLine[1].trim();
          String actualVal = new PropertyValue(val).getActual();
          
          // if we encounter an "include" statement - load the new file
          if (key.equalsIgnoreCase(cIncludeKey))
          {
            load(actualVal, properties);
          }
          else
          {
            properties.put(key, actualVal);
          }
        }
      }
      
      // add file to list of monitored files
      String included = (String)properties.get(cIncludeKey);
      if (included == null)
      {
        included = fileName;
      }
      else
      {
        included = included + "," + fileName;
      }
      properties.put(cIncludeKey, included);
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected String getPadding(int level)
  {
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < level; ++i)
      sb.append("  ");
    
    return sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String name()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = getPadding(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(StringUtils.asPrintableString(this, level + 1)).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
