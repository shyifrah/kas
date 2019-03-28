package com.kas.infra.base;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.config.IConfiguration;
import com.kas.infra.utils.StringUtils;

/**
 * KAS {@link Properties} class has the ability to load from a file,
 * interpret an "include" statements and resolve variables.
 * 
 * @author Pippo
 */
public class Properties extends ConcurrentHashMap<Object, Object> implements ISerializable
{
  static private final long serialVersionUID = 1L;
  static private Logger sLogger = LogManager.getLogger(Properties.class);
  
  /**
   * Construct an empty set of {@link Properties}
   */
  public Properties()
  {
    super();
  }
  
  /**
   * Construct a set of {@link Properties} from a map.<br>
   * After construction, this {@link Properties} object will have the same entries as {@code map}.
   * 
   * @param map
   *   The map.
   */
  public Properties(Map<?, ?> other)
  {
    super(other);
  }
  
  /**
   * Constructs a set of {@link Properties} object from {@link ObjectInputStream}
   * 
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   If I/O error occurs
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
   * @param ostream
   *   The {@link ObjectOutputStream} to which the properties will be serialized
   * @throws IOException
   *   If an I/O error occurs
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
   * If {@code key} is {@code null}, a return value of {@code null} is returned.<br>
   * Otherwise, the returned value is the same as defined in {@link java.util.concurrent.ConcurrentHashMap#get(Object)}.
   * 
   * @param key
   *   The property's key.
   * @return
   *   the property value
   */
  public Object getProperty(Object key)
  {
    if (key == null)
      return null;
    
    return super.get(key);
  }
  
  /**
   * Get a Boolean property.<br>
   * If the property does not exist, an exception is thrown.<br>
   * If the property does not designate a valid {@code boolean} value, {@code false} is returned. 
   * 
   * @param key
   *   The name of the property
   * @return
   *   {@code true} if the value is not null and is equal, ignoring case, to the string "true". {@code false} otherwise
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public boolean getBoolProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getBoolProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getBoolProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Boolean property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    sLogger.trace("Properties::getBoolProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    boolean result = defaultValue;
    try
    {
      result = getBoolProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getBoolProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a Boolean property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setBoolProperty(String key, boolean value)
  {
    sLogger.trace("Properties::setBoolProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, Boolean.valueOf(value));
    
    sLogger.trace("Properties::setBoolProperty() - OUT");
  }
  
  /**
   * Get a Character property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code char} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Char} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public char getCharProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getCharProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, strResult);
    }
    catch (Throwable e)
    {
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getCharProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Character property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public char getCharProperty(String key, char defaultValue)
  {
    sLogger.trace("Properties::getCharProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    char result = defaultValue;
    try
    {
      result = getCharProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getCharProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set an Character property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setCharProperty(String key, char value)
  {
    sLogger.trace("Properties::setCharProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setCharProperty() - OUT");
  }
  
  /**
   * Get a Integer property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code int} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Integer} value 
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public int getIntProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getIntProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getIntProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Integer property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public int getIntProperty(String key, int defaultValue)
  {
    sLogger.trace("Properties::getIntProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    int result = defaultValue;
    try
    {
      result = getIntProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getIntProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set an Integer property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setIntProperty(String key, int value)
  {
    sLogger.trace("Properties::setIntProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setIntProperty() - OUT");
  }
  
  /**
   * Get a String property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link String} value or the {@link Object}'s {@link java.lang.Object#toString()} value 
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public String getStringProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getStringProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getStringProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a String property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public String getStringProperty(String key, String defaultValue)
  {
    sLogger.trace("Properties::getStringProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    String result = defaultValue;
    try
    {
      result = getStringProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getStringProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a String property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setStringProperty(String key, String value)
  {
    sLogger.trace("Properties::setStringProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setStringProperty() - OUT");
  }
  
  /**
   * Get a Long property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code long} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Long} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public long getLongProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getLongProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getLongProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Long property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public long getLongProperty(String key, long defaultValue)
  {
    sLogger.trace("Properties::getLongProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    long result = defaultValue;
    try
    {
      result = getLongProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getLongProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a Long property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setLongProperty(String key, long value)
  {
    sLogger.trace("Properties::setLongProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setLongProperty() - OUT");
  }
  
  /**
   * Get a Byte property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code byte} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Byte} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public byte getByteProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getByteProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getByteProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Byte property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public byte getByteProperty(String key, byte defaultValue)
  {
    sLogger.trace("Properties::getByteProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    byte result = defaultValue;
    try
    {
      result = getByteProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getByteProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a Byte property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setByteProperty(String key, byte value)
  {
    sLogger.trace("Properties::setByteProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setByteProperty() - OUT");
  }
  
  /**
   * Get a Bytes (byte []) property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code byte} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   an array of bytes value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public byte [] getBytesProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getBytesProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
    byte [] result;
    try
    {
      result = (byte [])objResult;
    }
    catch (Throwable e)
    {
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getBytesProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a byte [] property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public byte [] getBytesProperty(String key, byte [] defaultValue)
  {
    sLogger.trace("Properties::getBytesProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    byte [] result = defaultValue;
    try
    {
      result = getBytesProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getBytesProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a byte [] property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setBytesProperty(String key, byte [] value)
  {
    sLogger.trace("Properties::setBytesProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setBytesProperty() - OUT");
  }
  
  /**
   * Set a byte [] property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   * @param offset
   *   The offset from the beginning of the array
   * @param length
   *   The length to copy
   */
  public void setBytesProperty(String key, byte [] value, int offset, int length)
  {
    sLogger.trace("Properties::setBytesProperty() - IN, Key={}, Value={}, Offset={}, Length={}", key, value, offset, length);
    
    byte [] subarray = new byte [length];
    System.arraycopy(value, offset, subarray, 0, length);
    put(key, subarray);
    
    sLogger.trace("Properties::setBytesProperty() - OUT");
  }
  
  /**
   * Get a Double property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code double} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Double} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public double getDoubleProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getDoubleProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getDoubleProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Double property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public double getDoubleProperty(String key, double defaultValue)
  {
    sLogger.trace("Properties::getDoubleProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    double result = defaultValue;
    try
    {
      result = getLongProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getDoubleProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a Double property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setDoubleProperty(String key, double value)
  {
    sLogger.trace("Properties::setDoubleProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setDoubleProperty() - OUT");
  }
  
  /**
   * Get a Float property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code float} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Float} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public float getFloatProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getFloatProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getFloatProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Float property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public float getFloatProperty(String key, float defaultValue)
  {
    sLogger.trace("Properties::getFloatProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    float result = defaultValue;
    try
    {
      result = getFloatProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getFloatProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a Float property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setFloatProperty(String key, float value)
  {
    sLogger.trace("Properties::setFloatProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setFloatProperty() - OUT");
  }
  
  /**
   * Get a Object property.<br>
   * <br>
   * If the property does not exist, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Object} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public Object getObjectProperty(String key) throws PropertyNotFoundException
  {
    sLogger.trace("Properties::getObjectProperty() - IN, Key={}", key);
    
    Object result = getProperty(key);
    if (result == null)
      throw new PropertyNotFoundException(key);
    
    sLogger.trace("Properties::getObjectProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Object property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public Object getObjectProperty(String key, Object defaultValue)
  {
    sLogger.trace("Properties::getObjectProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    Object result = defaultValue;
    try
    {
      result = getObjectProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getObjectProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set an Object property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setObjectProperty(String key, Object value)
  {
    sLogger.trace("Properties::setObjectProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setObjectProperty() - OUT");
  }
  
  /**
   * Get a Short property.<br>
   * <br>
   * If the property does not exist, or property does not designate a valid {@code short} value, an exception is thrown.
   * 
   * @param key
   *   The name of the property
   * @return
   *   the {@link Short} value
   * @throws {@link PropertyNotFoundException}
   *   if property is not found
   * @throws {@link InvalidPropertyValueException}
   *   if other error occurred
   */
  public short getShortProperty(String key) throws PropertyNotFoundException, InvalidPropertyValueException
  {
    sLogger.trace("Properties::getShortProperty() - IN, Key={}", key);
    
    Object objResult = getProperty(key);
    if (objResult == null)
      throw new PropertyNotFoundException(key);
    
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
      throw new InvalidPropertyValueException(key, objResult);
    }
    
    sLogger.trace("Properties::getShortProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Get a Short property with default value if one is not present
   * 
   * @param key
   *   The name of the property
   * @param defaultValue
   *   The default value of the property
   * @return
   *   the property value, or {@code defaultValue} if one is not present
   */
  public short getShortProperty(String key, short defaultValue)
  {
    sLogger.trace("Properties::getShortProperty() - IN, Key={}, Default={}", key, defaultValue);
    
    short result = defaultValue;
    try
    {
      result = getShortProperty(key);
    }
    catch (Throwable e) {}
    
    sLogger.trace("Properties::getShortProperty() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Set a Short property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setShortProperty(String key, short value)
  {
    sLogger.trace("Properties::setShortProperty() - IN, Key={}, Value={}", key, value);
    
    put(key, value);
    
    sLogger.trace("Properties::setShortProperty() - OUT");
  }
  
  /**
   * Return a subset of the entire {@link Properties} object.<br>
   * <br>
   * All properties in the subset have a common prefix specified by {@code keyPrefix}.<br>
   * For a {@code null} or empty string, all contents are replicated.
   * 
   * @param keyPrefix
   *   The prefix of the keys to include in the subset
   * @return
   *   a new {@link Properties} object including only keys that are prefixed with {@code keyPrefix}
   */
  public Properties getSubset(String keyPrefix)
  {
    sLogger.trace("Properties::getSubset() - IN, KeyPrefix={}", keyPrefix);
    
    Properties result = getSubset(keyPrefix, "");
    
    sLogger.trace("Properties::getSubset() - OUT, Result={}", result);
    return result;
  }

  /**
   * Get a subset of the {@link IConfiguration} object.<br>
   * <br>
   * All properties in the subset have a common prefix specified by {@code keyPrefix}
   * and a common suffix specified by {@code keySuffix}.<br>
   * For a {@code null} or empty string, all contents are replicated.
   * 
   * @param keyPrefix
   *   The prefix of the keys to include in the subset
   * @param keySuffix
   *   The suffix of the keys to include in the subset
   * @return
   *   a new {@link Properties} object including only keys that are prefixed
   *   with {@code keyPrefix} <b>AND</b> suffixed with {@code keySuffix}
   */
  public Properties getSubset(String keyPrefix, String keySuffix)
  {
    sLogger.trace("Properties::getSubset() - IN, KeyPrefix={}, KeySuffix={}", keyPrefix, keySuffix);
    
    Properties result = new Properties();
    result.putAll(this);
    String pref = keyPrefix == null ? "" : keyPrefix;
    String suff = keySuffix == null ? "" : keySuffix;
    
    for (Map.Entry<Object, Object> entry : entrySet())
    {
      String key = (String)entry.getKey();
      if (!key.startsWith(pref))
        result.remove(key);
      else if (!key.endsWith(suff))
        result.remove(key);
    }
    
    sLogger.trace("Properties::getSubset() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Returns the {@link Properties} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public String name()
  {
    return StringUtils.getClassName(getClass());
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level
   *   The string padding level
   * @return
   *   the string representation with the specified level of padding
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
