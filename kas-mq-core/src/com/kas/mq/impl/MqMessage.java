package com.kas.mq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ERequestType;

/**
 * A KAS/MQ base message.<br>
 * <br>
 * Each message has an unique ID - that differentiates it from other messages in this KAS/MQ system, a payload,
 * and a few other characteristics such as priority that determines the behavior of KAS/MQ system when processing the message.
 * 
 * @author Pippo
 */
public class MqMessage extends AKasObject implements IPacket
{
  /**
   * The message priority
   */
  protected int mPriority;
  
  /**
   * The message unique identifier
   */
  protected UniqueId mMessageId;
  
  /**
   * Request type
   */
  protected ERequestType mRequestType = ERequestType.cUnknown;;
  
  /**
   * Message properties
   */
  protected Properties mProperties;
  
  /**
   * Construct a default message object
   */
  MqMessage()
  {
    mPriority  = IMqConstants.cDefaultPriority;
    mMessageId = UniqueId.generate();
    mProperties = new Properties();
  }
  
  /**
   * Constructs a {@link MqMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqMessage(ObjectInputStream istream) throws IOException
  {
    try
    {
      mPriority = istream.readInt();
      
      byte [] ba = new byte [16];
      istream.read(ba);
      mMessageId = UniqueId.fromByteArray(ba);
      
      int reqType = istream.readInt();
      mRequestType = ERequestType.fromInt(reqType);
      
      mProperties = new Properties(istream);
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
   * Serialize the {@link MqMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    ostream.writeInt(mPriority);
    ostream.reset();
    
    byte [] ba = mMessageId.toByteArray();
    ostream.write(ba);
    ostream.reset();
    
    ostream.writeInt(mRequestType.ordinal());
    ostream.reset();
    
    mProperties.serialize(ostream);
  }
  
  /**
   * Get the message Id
   * 
   * @return the message id
   */
  public UniqueId getMessageId()
  {
    return mMessageId;
  }
  
  /**
   * Set the message priority
   * 
   * @param priority The message priority
   * 
   * @throws IllegalArgumentException if the new priority is invalid
   */
  public void setPriority(int priority)
  {
    if ((priority < IMqConstants.cMinimumPriority) || (priority > IMqConstants.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    mPriority = priority;
  }
  
  /**
   * Get the message priority
   * 
   * @return the message priority
   */
  public int getPriority()
  {
    return mPriority;
  }

  /**
   * Set the request type.<br>
   * <br>
   * Note that this property must be set for administrative messages.
   * 
   * @param type The {@link ERequestType} of the message
   */
  public void setRequestType(ERequestType type)
  {
    mRequestType = type;
  }
  
  /**
   * Get the administrative message's request type
   * 
   * @return the administrative message's request type, or {@link EMqResponseCode.cUnknown} for non-administrative messages
   */
  public ERequestType getRequestType()
  {
    return mRequestType;
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqMessage);
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
    return mProperties.getObjectProperty(key, defaultValue);
  }
  
  /**
   * Set a Object property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setObjectProperty(String key, Object value)
  {
    mProperties.setObjectProperty(key, value);
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
    return mProperties.getBoolProperty(key, defaultValue);
  }
  
  /**
   * Set a Boolean property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setBoolProperty(String key, boolean value)
  {
    mProperties.setBoolProperty(key, value);
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
    return mProperties.getStringProperty(key, defaultValue);
  }
  
  /**
   * Set a String property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setStringProperty(String key, String value)
  {
    mProperties.setStringProperty(key, value);
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
    return mProperties.getByteProperty(key, defaultValue);
  }
  
  /**
   * Set a Byte property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setByteProperty(String key, byte value)
  {
    mProperties.setByteProperty(key, value);
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
    return mProperties.getShortProperty(key, defaultValue);
  }
  
  /**
   * Set a Short property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setShortProperty(String key, short value)
  {
    mProperties.setShortProperty(key, value);
  }
  
  /**
   * Get an Integer property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public int getIntProperty(String key, int defaultValue)
  {
    return mProperties.getIntProperty(key, defaultValue);
  }
  
  /**
   * Set an Integer property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setIntProperty(String key, int value)
  {
    mProperties.setIntProperty(key, value);
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
    return mProperties.getLongProperty(key, defaultValue);
  }
  
  /**
   * Set a Long property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setLongProperty(String key, long value)
  {
    mProperties.setLongProperty(key, value);
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
    return mProperties.getFloatProperty(key, defaultValue);
  }
  
  /**
   * Set a Float property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setFloatProperty(String key, float value)
  {
    mProperties.setFloatProperty(key, value);
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
    return mProperties.getDoubleProperty(key, defaultValue);
  }
  
  /**
   * Set a Double property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public void setDoubleProperty(String key, double value)
  {
    mProperties.setDoubleProperty(key, value);
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
      .append(pad).append("  Message Id=").append(mMessageId.toPrintableString()).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append("  Request Type=").append(StringUtils.asPrintableString(mRequestType)).append("\n")
      .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
