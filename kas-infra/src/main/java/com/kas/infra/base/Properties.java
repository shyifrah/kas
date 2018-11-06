package com.kas.infra.base;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.config.IConfiguration;
import com.kas.infra.utils.StringUtils;

/**
 * KAS {@link Properties} class has the ability to load from a file and interpret
 * an "include" statement
 * 
 * @author Pippo
 */
public class Properties extends ConcurrentHashMap<Object, Object> implements ISerializable
{
  static private final long   serialVersionUID = 1L;
  
  /**
   * Construct an empty set of {@link Properties}
   */
  public Properties()
  {
    super();
  }
  
  /**
   * Construct a set of {@link Properties} from a map.<br>
   * <br>
   * After construction, this {@link Properties} object will have the same entries as {@code map}.
   * 
   * @param map The map.
   */
  public Properties(Map<?, ?> other)
  {
    super(other);
  }
  
  /**
   * Constructs a set of {@link Properties} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public Properties(ObjectInputStream istream) throws IOException
  {
    try
    {
      int totalEntries = istream.readInt();
      for (int i = 0; i < totalEntries; ++i)
      {
        String name = (String)istream.readObject();
        Object value = istream.readObject();
        put(name, value);
      }
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Throwable e)
    {
      throw new IOException(e);
    }
  }
  
  /**
   * Serialize the {@link Properties} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the properties will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public synchronized void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeInt(size());
    ostream.reset();
    
    for (Map.Entry<Object, Object> entry : entrySet())
    {
      String name = (String)entry.getKey();
      Object value = entry.getValue();
      
      ostream.writeObject(name);
      ostream.reset();
      ostream.writeObject(value);
      ostream.reset();
    }
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
   * 
   * @see com.kas.infra.config.IConfiguration#getBoolProperty(String, boolean)
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
    put(key, Boolean.valueOf(value));
  }
  
  /**
   * Get a Character property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code char} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link Char} value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
  public char getCharProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getCharProperty() - Property not found: " + key);
    
    Character result;
    try
    {
      result = (Character)objResult;
    }
    catch (ClassCastException e)
    {
      String strResult = (String)objResult;
      if (strResult.length() == 1)
        result = strResult.charAt(0);
      throw new KasException("getCharProperty() - Invalid value: " + strResult);
    }
    catch (Throwable e)
    {
      throw new KasException("getCharProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  /**
   * Get a Character property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.infra.config.IConfiguration#getIntProperty(String, int)
   */
  public char getCharProperty(String key, char defaultValue)
  {
    char value = defaultValue;
    try
    {
      value = getCharProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  /**
   * Set an Character property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setCharProperty(String key, char value)
  {
    put(key, value);
  }
  
  /**
   * Get a Integer property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code int} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link Integer} value
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
   * 
   * @see com.kas.infra.config.IConfiguration#getIntProperty(String, int)
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
    put(key, value);
  }
  
  /**
   * Get a String property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link String} value or the {@link Object}'s {@link java.lang.Object#toString()} value 
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
   * 
   * @see com.kas.infra.config.IConfiguration#getStringProperty(String, String)
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
   * @return the {@link Long} value
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
   * 
   * @see com.kas.infra.config.IConfiguration#getLongProperty(String, long)
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
    put(key, value);
  }
  
  /**
   * Get a Byte property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code byte} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link Byte} value
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
    put(key, value);
  }
  
  /**
   * Get a Bytes (byte []) property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code byte} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the an array of bytes value
   * 
   * @throws {@link KasException} if property is not found or some other error occurred
   */
  public byte [] getBytesProperty(String key) throws KasException
  {
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new KasException("getBytesProperty() - Property not found: " + key);
    
    byte [] result;
    try
    {
      result = (byte [])objResult;
    }
    catch (Throwable e)
    {
      throw new KasException("getBytesProperty() - Invalid value: " + objResult);
    }
    
    return result;
  }
  
  /**
   * Get a byte [] property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public byte [] getBytesProperty(String key, byte [] defaultValue)
  {
    byte [] value = defaultValue;
    try
    {
      value = getBytesProperty(key);
    }
    catch (Throwable e) {}
    return value;
  }
  
  /**
   * Set a byte [] property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBytesProperty(String key, byte [] value)
  {
    put(key, value);
  }
  
  /**
   * Set a Byte property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBytesProperty(String key, byte [] value, int offset, int length)
  {
    byte [] subarray = new byte [length];
    System.arraycopy(value, offset, subarray, 0, length);
    put(key, subarray);
  }
  
  /**
   * Get a Double property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code double} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link Double} value
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
    put(key, value);
  }
  
  /**
   * Get a Float property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code float} value, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link Float} value
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
    put(key, value);
  }
  
  /**
   * Get a Object property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.
   * 
   * @param key The name of the property
   * @return the {@link Object} value
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
   * @return the {@link Short} value
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
    put(key, value);
  }
  
  /**
   * Return a subset of the entire {@link Properties} object.<br>
   * <br>
   * All properties in the subset have a common prefix specified by {@code keyPrefix}.<br>
   * For a {@code null} or empty string, all contents are replicated.
   * 
   * @param keyPrefix The prefix of the keys to include in the subset
   * @return a new {@link Properties} object including only keys that are prefixed with {@code keyPrefix}
   * 
   * @see com.kas.infra.config.IConfiguration#getSubset(String)
   */
  public Properties getSubset(String keyPrefix)
  {
    return getSubset(keyPrefix, "");
  }

  /**
   * Get a subset of the {@link IConfiguration} object.<br>
   * <br>
   * All properties in the subset have a common prefix specified by {@code keyPrefix}
   * and a common suffix specified by {@code keySuffix}.<br>
   * For a {@code null} or empty string, all contents are replicated.
   * 
   * @param keyPrefix The prefix of the keys to include in the subset
   * @param keySuffix The suffix of the keys to include in the subset
   * @return a new {@link Properties} object including only keys that are prefixed
   * with {@code keyPrefix} <b>AND</b> suffixed with {@code keySuffix}
   */
  public Properties getSubset(String keyPrefix, String keySuffix)
  {
    Properties subset = new Properties();
    subset.putAll(this);
    String pref = keyPrefix == null ? "" : keyPrefix;
    String suff = keySuffix == null ? "" : keySuffix;
    
    for (Map.Entry<Object, Object> entry : entrySet())
    {
      String key = (String)entry.getKey();
      if (!key.startsWith(pref))
        subset.remove(key);
      else if (!key.endsWith(suff))
        subset.remove(key);
    }
    
    return subset;
  }
  
  /**
   * Returns the {@link Properties} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public String name()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
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
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(StringUtils.asPrintableString(this, level + 1)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
