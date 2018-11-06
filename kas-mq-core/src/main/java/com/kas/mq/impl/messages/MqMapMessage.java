package com.kas.mq.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Map;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ABaseMessage;

/**
 * A KAS/MQ message with a map payload.<br>
 * <br>
 * The message body is a single {@link Map} object
 * 
 * @author Pippo
 */
public final class MqMapMessage extends ABaseMessage
{
  /**
   * The message body
   */
  private Properties mBody;
  
  /**
   * Construct a default text message object
   */
  MqMapMessage()
  {
    super();
    mBody = null;
  }
  
  /**
   * Construct a default text message object
   */
  MqMapMessage(Map<?, ?> map)
  {
    super();
    mBody = new Properties(map);
  }
  
  /**
   * Constructs a {@link MqMapMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqMapMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    
    mBody = new Properties(istream);
  }
  
  /**
   * Serialize the {@link MqMapMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    super.serialize(ostream);
    
    mProperties.serialize(ostream);
  }
  
  /**
   * Get an {@link Enumeration} of all keys
   * 
   * @return an {@link Enumeration} of all keys
   */
  public Enumeration<?> getMapNames()
  {
    return mBody.keys();
  }
  
  /**
   * Check if {@code name} is a key in the map
   * 
   * @param name The key to be checked
   * @return {@code true} if {@code name} is a key in the map, {@code false} otherwise
   */
  public boolean itemExists(String name)
  {
    return mBody.containsKey(name);
  }
  
  /**
   * Get a Boolean property.
   * 
   * @param key The name of the property
   * @return {@code true} if the value is not null and is equal, ignoring case, to the string "true". {@code false} otherwise
   */
  public boolean getBoolean(String key)
  {
    return mBody.getBoolProperty(key, false);
  }
  
  /**
   * Set a Boolean property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBoolean(String key, boolean value)
  {
    mBody.setBoolProperty(key, value);
  }
  
  /**
   * Get a Integer property.
   * 
   * @param key The name of the property
   * @return the {@link Integer} value
   */
  public int getInt(String key)
  {
    return mBody.getIntProperty(key, -1);
  }
  
  /**
   * Set an Integer property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setInt(String key, int value)
  {
    mBody.setIntProperty(key, value);
  }
  
  /**
   * Get a Character property.
   * 
   * @param key The name of the property
   * @return the {@link Char} value
   */
  public char getChar(String key)
  {
    return mBody.getCharProperty(key, ' ');
  }
  
  /**
   * Set a Character property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setChar(String key, char value)
  {
    mBody.setCharProperty(key, value);
  }
  
  /**
   * Get a String property.
   * 
   * @param key The name of the property
   * @return the {@link String} value or the {@link Object}'s {@link java.lang.Object#toString()} value
   */
  public String getString(String key)
  {
    return mBody.getStringProperty(key, null);
  }
  
  /**
   * Set a String property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setString(String key, String value)
  {
    mBody.setStringProperty(key, value);
  }
  
  /**
   * Get a Long property
   * 
   * @param key The name of the property
   * @return the {@link Long} value
   */
  public long getLong(String key)
  {
    return mBody.getLongProperty(key, -1L);
  }
  
  /**
   * Set a Long property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setLong(String key, long value)
  {
    mBody.setLongProperty(key, value);
  }
  
  /**
   * Get a Byte property.
   * 
   * @param key The name of the property
   * @return the {@link Byte} value
   */
  public byte getByte(String key)
  {
    return mBody.getByteProperty(key, (byte)0);
  }
  
  /**
   * Set a Byte property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setByte(String key, byte value)
  {
    mBody.setByteProperty(key, value);
  }
  
  /**
   * Get a byte [] property.
   * 
   * @param key The name of the property
   * @return the byte array value
   */
  public byte [] getBytes(String key)
  {
    return mBody.getBytesProperty(key, null);
  }
  
  /**
   * Set a byte [] property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBytes(String key, byte [] value)
  {
    mBody.setBytesProperty(key, value);
  }
  
  /**
   * Set a byte [] property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBytes(String key, byte [] value, int offset, int length)
  {
    mBody.setBytesProperty(key, value, offset, length);
  }
  
  /**
   * Get a Double property.
   * 
   * @param key The name of the property
   * @return the {@link Double} value
   */
  public double getDouble(String key)
  {
    return mBody.getDoubleProperty(key, -1);
  }
  
  /**
   * Set a Double property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setDouble(String key, double value)
  {
    mBody.setDoubleProperty(key, value);
  }
  
  /**
   * Get a Float property.<
   * 
   * @param key The name of the property
   * @return the {@link Float} value
   */
  public float getFloat(String key)
  {
    return mBody.getFloatProperty(key, -1);
  }
  
  /**
   * Set a Float property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setFloat(String key, float value)
  {
    mBody.setFloatProperty(key, value);
  }
  
  /**
   * Get a Object property.
   * 
   * @param key The name of the property
   * @return the {@link Object} value
   */
  public Object getObject(String key)
  {
    return mBody.getObjectProperty(key, null);
  }
  
  /**
   * Set an Object property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setObject(String key, Object value)
  {
    mBody.setObjectProperty(key, value);
  }
  
  /**
   * Get a Short property.
   * 
   * @param key The name of the property
   * @return the {@link Short} value
   */
  public short getShort(String key)
  {
    return mBody.getShortProperty(key, (short)-1);
  }
  
  /**
   * Set a Short property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setShort(String key, short value)
  {
    mBody.setShortProperty(key, value);
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqMapMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqMapMessage);
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(super.toPrintableString(level))
      .append(pad).append("  Body=(").append(StringUtils.asString(mBody)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
