package com.kas.infra.base;

import java.io.File;
import java.util.List;
import java.util.Map;
import com.kas.infra.config.IConfiguration;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.StringUtils;

public class Properties extends java.util.Properties implements IConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static final long serialVersionUID = 1L;
  
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
  public Properties(java.util.Properties props)
  {
    super.putAll(props);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getProperty(String key)
  {
    if (key == null)
      return null;
    
    return super.getProperty(key);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean getBoolProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getBoolProperty() - Property not found: " + key);
    }
    
    if (!("false".equalsIgnoreCase(strResult)) && !("true".equalsIgnoreCase(strResult)))
    {
      throw new KasException("getBoolProperty() - Invalid value: " + strResult);
    }
    return Boolean.valueOf(strResult);
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
    super.setProperty(key, Boolean.toString(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getIntProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getIntProperty() - Property not found: " + key);
    }
    
    int value;
    try
    {
      value = Integer.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getIntProperty() - Exception caught", e);
    }
    return value;
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
    super.setProperty(key, Integer.toString(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getStringProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getStringProperty() - Property not found: " + key);
    }
    
    return strResult;
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
    super.setProperty(key, value);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public long getLongProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getLongProperty() - Property not found: " + key);
    }
    
    long value;
    try
    {
      value = Long.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getLongProperty() - Exception caught", e);
    }
    return value;
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
    super.setProperty(key, Long.toString(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public byte getByteProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getByteProperty() - Property not found: " + key);
    }
    
    byte value;
    try
    {
      value = Byte.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getByteProperty() - Exception caught", e);
    }
    return value;
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
    super.setProperty(key, Byte.toString(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public double getDoubleProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getDoubleProperty() - Property not found: " + key);
    }
    
    double value;
    try
    {
      value = Double.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getDoubleProperty() - Exception caught", e);
    }
    return value;
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
    super.setProperty(key, Double.toString(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public float getFloatProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getFloatProperty() - Property not found: " + key);
    }
    
    float value;
    try
    {
      value = Float.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getFloatProperty() - Exception caught", e);
    }
    return value;
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
    super.setProperty(key, Float.toString(value));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Object getObjectProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getObjectProperty() - Property not found: " + key);
    }
    
    Object value = strResult;
    return value;
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
    super.setProperty(key, value.toString());
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public short getShortProperty(String key) throws KasException
  {
    String strResult = getProperty(key);
    if (strResult == null)
    {
      throw new KasException("getShortProperty() - Property not found: " + key);
    }
    
    short value;
    try
    {
      value = Short.valueOf(strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getShortProperty() - Exception caught", e);
    }
    return value;
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
    super.setProperty(key, Short.toString(value));
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
    
    return new Properties(subset);
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
  private void load(String fileName, java.util.Properties properties)
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
          if (key.equalsIgnoreCase(Constants.cIncludeKey))
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
      String included = (String)properties.get(Constants.cIncludeKey);
      if (included == null)
      {
        included = fileName;
      }
      else
      {
        included = included + "," + fileName;
      }
      properties.put(Constants.cIncludeKey, included);
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
