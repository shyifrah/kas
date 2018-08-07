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
  static public  final String cIncludeKey      = "kas.include";
  static private final long   serialVersionUID = 1L;
  
  /**
   * Construct an empty set of properties
   */
  public Properties()
  {
    super();
  }
  
  /**
   * Construct a set of properties from a different set.<br>
   * <br>
   * After construction, this {@link Properties} object will have the same contents as {@code other}.
   * 
   * @param other The other {@link Properties} object.
   */
  public Properties(Properties other)
  {
    super(other);
  }
  
  /**
   * Get a property from the map.<br>
   * <br>
   * If {@code key} is {@code null}, a return value of {@code null} is returned.<br>
   * Otherwise, the returned value is the same as defined in {@link java.util.concurrent.ConcurrentHashMap#get(Object)}.
   * 
   * @param key The property's key.
   * @return the property value
   */
  public Object getProperty(Object key)
  {
    if (key == null)
      return null;
    
    return super.get(key);
  }
  
  /**
   * Get a Boolean property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.<br>
   * If the property does not designate a valid {@code boolean} value, {@code false} is returned. 
   * 
   * @param key The name of the property
   * @return {@code true} if the value is not null and is equal, ignoring case, to the string "true". {@code false} otherwise
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Boolean property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a Boolean property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBoolProperty(String key, boolean value)
  {
    put(key, new Boolean(value));
  }
  
  /**
   * Get a Integer property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code int} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Integer} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Integer property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set an Integer property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setIntProperty(String key, int value)
  {
    put(key, new Integer(value));
  }
  
  /**
   * Get a String property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code String} value or the {@code Object}'s {@link java.lang.Object#toString()} value 
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a String property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a String property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setStringProperty(String key, String value)
  {
    put(key, value);
  }
  
  /**
   * Get a Long property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code long} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Long} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Long property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a Long property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setLongProperty(String key, long value)
  {
    put(key, new Long(value));
  }
  
  /**
   * Get a Byte property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code byte} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Byte} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Byte property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a Byte property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setByteProperty(String key, byte value)
  {
    put(key, new Byte(value));
  }
  
  /**
   * Get a Double property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code double} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Double} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Double property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a Double property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setDoubleProperty(String key, double value)
  {
    put(key, new Double(value));
  }
  
  /**
   * Get a Float property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code float} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Float} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Float property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a Float property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setFloatProperty(String key, float value)
  {
    put(key, new Float(value));
  }
  
  /**
   * Get a Object property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Object} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
  public Object getObjectProperty(String key) throws KasException
  {
    Object result = getProperty(key);
    if (result == null)
      throw new KasException("getObjectProperty() - Property not found: " + key);
    
    return result;
  }
  
  /**
   * Get a Object property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set an Object property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setObjectProperty(String key, Object value)
  {
    put(key, value);
  }
  
  /**
   * Get a Short property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code short} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@code Short} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
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
  
  /**
   * Get a Short property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
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
  
  /**
   * Set a Short property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setShortProperty(String key, short value)
  {
    put(key, new Short(value));
  }
  
  /**
   * Return a subset of the entire {@link Properties} object.<br>
   * <br>
   * All properties in the subset have a common prefix specified by {@code keyPrefix}.
   * 
   * @param keyPrefix The prefix of the keys to include in the subset
   * @return a new {@link Properties} object including only keys that are prefixed with {@code keyPrefix}
   */
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

  /**
   * Load the properties defined in file {@code fileName} into this {@link Properties} object.<br>
   * <br>
   * This method is used by outside callers to allow properties refresh.  
   * 
   * @param fileName The fully-pathed name of the file to be loaded
   */
  public void load(String fileName)
  {
    load(fileName, this);
  }
  
  /**
   * Load the properties defined in file {@code fileName} into {@code properties}.<br>
   * <br>
   * Lines in {@code fileName} are processed one at a time. Comments are ignored and lines are checked
   * to have valid syntax. That is, non-comment lines should have a format of key=value.<br>
   * When {@code include} statement is encountered, it actually points to a new file that should be loaded as well.  
   * 
   * @param fileName The fully-pathed name of the file to be loaded
   * @param properties The {@link Properties} object into which keys will be mapped to values.
   */
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
  
  /**
   * Returns the {@link Properties} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public String name()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }
  
  /**
   * Returns a replica of this {@link Properties}.
   * 
   * @return a replica of this {@link Properties}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public Properties replicate()
  {
    return new Properties(this);
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(StringUtils.asPrintableString(this, level + 1)).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
