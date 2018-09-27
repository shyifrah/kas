package com.kas.mq.impl.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;

/**
 * A KAS/MQ base message.<br>
 * <br>
 * Each message has an unique ID - that differentiates it from other messages in this KAS/MQ system, a payload,
 * and a few other characteristics such as priority that determines the behavior of KAS/MQ system when processing the message.
 * 
 * @author Pippo
 */
public abstract class AMqMessage extends AKasObject implements IPacket, IMqMessage
{
  /**
   * The message unique identifier
   */
  protected UniqueId mMessageId;
  
  /**
   * The response identifier
   */
  protected UniqueId mReferenceId;
  
  /**
   * The message priority
   */
  protected int mPriority;
  
  /**
   * Request type
   */
  protected ERequestType mRequestType = ERequestType.cUnknown;;
  
  /**
   * When message was created
   */
  protected long mTimeStamp;
  
  /**
   * Expiration
   */
  protected long mExpiration;
  
  /**
   * A response
   */
  protected MqResponse mResponse;
  
  /**
   * Message properties
   */
  protected Properties mProperties;
  
  /**
   * Construct a default message object
   */
  public AMqMessage()
  {
    mMessageId = UniqueId.generate();
    mReferenceId = UniqueId.cNullUniqueId;
    mPriority  = IMqConstants.cDefaultPriority;
    mProperties = new Properties();
    mTimeStamp = System.currentTimeMillis();
    mExpiration = IMqConstants.cDefaultExpiration;
    mResponse = new MqResponse(EMqCode.cUnknown, -1, "");
  }
  
  /**
   * Constructs a {@link AMqMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public AMqMessage(ObjectInputStream istream) throws IOException
  {
    try
    {
      byte [] ba = new byte [16];
      istream.read(ba);
      mMessageId = UniqueId.fromByteArray(ba);
      
      istream.read(ba);
      mReferenceId = UniqueId.fromByteArray(ba);
      
      mPriority = istream.readInt();
      
      int reqType = istream.readInt();
      mRequestType = ERequestType.fromInt(reqType);
      
      mTimeStamp = istream.readLong();
      mExpiration = istream.readLong();
      
      mResponse = new MqResponse(istream);
      
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
   * Serialize the {@link AMqMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    byte [] ba = mMessageId.toByteArray();
    ostream.write(ba);
    ostream.reset();
    
    ba = mReferenceId.toByteArray();
    ostream.write(ba);
    ostream.reset();
    
    ostream.writeInt(mPriority);
    ostream.reset();
    
    ostream.writeInt(mRequestType.ordinal());
    ostream.reset();
    
    ostream.writeLong(mTimeStamp);
    ostream.reset();
    
    ostream.writeLong(mExpiration);
    ostream.reset();
    
    mResponse.serialize(ostream);
    
    mProperties.serialize(ostream);
  }
  
  /**
   * Get the message ID
   * 
   * @return the message id
   * 
   * @see com.kas.mq.impl.IMqMessage#getMessageId()
   */
  public UniqueId getMessageId()
  {
    return mMessageId;
  }
  
  /**
   * Set the reference ID
   * 
   * @param id The reference ID to set
   * 
   * @see com.kas.mq.impl.IMqMessage#setReferenceId(UniqueId)
   */
  public void setReferenceId(UniqueId id)
  {
    mReferenceId = id;
  }
  
  /**
   * Get the reference ID
   * 
   * @return the reference id
   * 
   * @see com.kas.mq.impl.IMqMessage#getReferenceId()
   */
  public UniqueId getReferenceId()
  {
    return mReferenceId;
  }
  
  /**
   * Set the message priority
   * 
   * @param priority The message priority
   * 
   * @throws IllegalArgumentException if the new priority is invalid
   * 
   * @see com.kas.mq.impl.IMqMessage#setPriority(int)
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
   * 
   * @see com.kas.mq.impl.IMqMessage#getPriority()
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
   * 
   * @see com.kas.mq.impl.IMqMessage#setRequestType(ERequestType)
   */
  public void setRequestType(ERequestType type)
  {
    mRequestType = type;
  }
  
  /**
   * Get the message's request type
   * 
   * @return the message's request type
   * 
   * @see com.kas.mq.impl.IMqMessage#getRequestType()
   */
  public ERequestType getRequestType()
  {
    return mRequestType;
  }
  
  /**
   * Get the timestamp at which the message was created
   * 
   * @return the timestamp at which the message was created
   * 
   * @see com.kas.mq.impl.IMqMessage#getTimeStamp()
   */
  public long getTimeStamp()
  {
    return mTimeStamp;
  }
  
  /**
   * Set the number of milliseconds from message creation till the message expires
   * 
   * @param exp The number of milliseconds from message creation till the message expires
   * 
   * @see com.kas.mq.impl.IMqMessage#setExpiration(long)
   */
  public void setExpiration(long exp)
  {
    mExpiration = exp;
  }
  
  /**
   * Get the number of milliseconds from message creation till the message expires
   * 
   * @return the number of milliseconds from message creation till the message expires
   * 
   * @see com.kas.mq.impl.IMqMessage#getExpiration()
   */
  public long getExpiration()
  {
    return mExpiration;
  }
  
  /**
   * An indicator whether the message is expired
   * 
   * @return {@code true} if message expired, {@code false} otherwise
   * 
   * @see com.kas.mq.impl.IMqMessage#isExpired()
   */
  public boolean isExpired()
  {
    long millisFromCreation = System.currentTimeMillis() - mTimeStamp;
    return millisFromCreation > mExpiration;
  }
  
  /**
   * Set the {@link MqResponse}
   * 
   * @param the {@link MqResponse} to set
   * 
   * @see com.kas.mq.impl.IMqMessage#setResponse(MqResponse)
   */
  public void setResponse(MqResponse resp)
  {
    mResponse = resp;
  }
  
  /**
   * Get the {@link MqResponse}
   * 
   * @return the {@link MqResponse}
   * 
   * @see com.kas.mq.impl.IMqMessage#getResponse()
   */
  public MqResponse getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set a Object property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setObjectProperty(String, Object)
   */
  public void setObjectProperty(String key, Object value)
  {
    mProperties.setObjectProperty(key, value);
  }
  
  /**
   * Get a Object property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getObjectProperty(String, Object)
   */
  public Object getObjectProperty(String key, Object defaultValue)
  {
    return mProperties.getObjectProperty(key, defaultValue);
  }
  
  /**
   * Set a Boolean property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setBoolProperty(String, boolean)
   */
  public void setBoolProperty(String key, boolean value)
  {
    mProperties.setBoolProperty(key, value);
  }
  
  /**
   * Get a Boolean property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getBoolProperty(String, boolean)
   */
  public boolean getBoolProperty(String key, boolean defaultValue)
  {
    return mProperties.getBoolProperty(key, defaultValue);
  }
  
  /**
   * Set a String property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setStringProperty(String, String)
   */
  public void setStringProperty(String key, String value)
  {
    mProperties.setStringProperty(key, value);
  }
  
  /**
   * Get a String property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getStringProperty(String, String)
   */
  public String getStringProperty(String key, String defaultValue)
  {
    return mProperties.getStringProperty(key, defaultValue);
  }
  
  /**
   * Set a Byte property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setByteProperty(String, byte)
   */
  public void setByteProperty(String key, byte value)
  {
    mProperties.setByteProperty(key, value);
  }
  
  /**
   * Get a Byte property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getByteProperty(String, byte)
   */
  public byte getByteProperty(String key, byte defaultValue)
  {
    return mProperties.getByteProperty(key, defaultValue);
  }
  
  /**
   * Set a Short property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setShortProperty(String, short)
   */
  public void setShortProperty(String key, short value)
  {
    mProperties.setShortProperty(key, value);
  }
  
  /**
   * Get a Short property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getShortProperty(String, short)
   */
  public short getShortProperty(String key, short defaultValue)
  {
    return mProperties.getShortProperty(key, defaultValue);
  }
  
  /**
   * Set an Integer property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setIntProperty(String, int)
   */
  public void setIntProperty(String key, int value)
  {
    mProperties.setIntProperty(key, value);
  }
  
  /**
   * Get an Integer property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getIntProperty(String, int)
   */
  public int getIntProperty(String key, int defaultValue)
  {
    return mProperties.getIntProperty(key, defaultValue);
  }
  
  /**
   * Set a Long property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setLongProperty(String, long)
   */
  public void setLongProperty(String key, long value)
  {
    mProperties.setLongProperty(key, value);
  }
  
  /**
   * Get a Long property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getLongProperty(String, long)
   */
  public long getLongProperty(String key, long defaultValue)
  {
    return mProperties.getLongProperty(key, defaultValue);
  }
  
  /**
   * Set a Float property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setFloatProperty(String, float)
   */
  public void setFloatProperty(String key, float value)
  {
    mProperties.setFloatProperty(key, value);
  }
  
  /**
   * Get a Float property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getFloatProperty(String, float)
   */
  public float getFloatProperty(String key, float defaultValue)
  {
    return mProperties.getFloatProperty(key, defaultValue);
  }
  
  /**
   * Set a Double property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   * 
   * @see com.kas.mq.impl.IMqMessage#setDoubleProperty(String, double)
   */
  public void setDoubleProperty(String key, double value)
  {
    mProperties.setDoubleProperty(key, value);
  }
  
  /**
   * Get a Double property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   * 
   * @see com.kas.mq.impl.IMqMessage#getDoubleProperty(String, double)
   */
  public double getDoubleProperty(String key, double defaultValue)
  {
    return mProperties.getDoubleProperty(key, defaultValue);
  }
  
  /**
   * Get a subset of the properties from the message's properties
   * 
   * @param prefix A string that all returned properties are prefixed with
   * @return a subset of the properties collection
   * 
   * @see com.kas.mq.impl.IMqMessage#getSubset(String)
   */
  public Properties getSubset(String prefix)
  {
    return mProperties.getSubset(prefix);
  }
  
  /**
   * Include a subset of properties in the message's properties
   * 
   * @param props A subset of {@link Properties} to include
   * @return the number of properties included
   * 
   * @see com.kas.mq.impl.IMqMessage#setSubset(Properties)
   */
  public void setSubset(Properties props)
  {
    if (props != null)
      mProperties.putAll(props);
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link AMqMessage}
   * 
   * @return the packet header
   * 
   * @see com.kas.comm.IPacket#createHeader()
   */
  public abstract PacketHeader createHeader();
  
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
    sb.append(pad).append("  Message Id=").append(mMessageId.toPrintableString()).append("\n")
      .append(pad).append("  Reference Id=").append(StringUtils.asPrintableString(mReferenceId)).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append("  Request Type=").append(StringUtils.asPrintableString(mRequestType)).append("\n")
      .append(pad).append("  TimeStamp=").append(mTimeStamp).append("\n")
      .append(pad).append("  Expiration=").append(mExpiration).append("\n")
      .append(pad).append("  Response=").append(mResponse.toPrintableString(level+1)).append("\n")
      .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level+1)).append(")\n");
    return sb.toString();
  }
}
