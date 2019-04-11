package com.kas.mq.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqResponse;

/**
 * A KAS/MQ base message, without a payload.<br>
 * Each message has an unique ID that differentiates it from other messages in
 * this KAS/MQ system, a few other characteristics such as priority or expiration,
 * that determines the behavior of KAS/MQ system when processing the message.<br>
 * Optionally, a message can have a payload (not this class, though).
 * 
 * @author Pippo
 */
public abstract class ABaseMessage extends AKasObject implements IMqMessage
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
   * Sub-Request type
   */
  protected ERequestSubType mRequestSubType = ERequestSubType.cUnknown;
  
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
   * Construct a default string message object
   */
  protected ABaseMessage()
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
   * Constructs a {@link ABaseMessage} object from {@link ObjectInputStream}
   * 
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   if I/O error occurs
   */
  public ABaseMessage(ObjectInputStream istream) throws IOException
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
   * Serialize the {@link ABaseMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream
   *   The {@link ObjectOutputStream} to which the message will be serialized
   * @throws IOException
   *   if an I/O error occurs
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
   * Create a header describing the packet
   * 
   * @return
   *   the header describing the packet
   * @throws IOException
   *   if an I/O error occurs
   */
  public abstract PacketHeader createHeader();
  
  /**
   * Get the message ID
   * 
   * @return
   *   the message id
   */
  public UniqueId getMessageId()
  {
    return mMessageId;
  }
  
  /**
   * Set the reference ID
   * 
   * @param id
   *   The reference ID to set
   */
  public void setReferenceId(UniqueId id)
  {
    mReferenceId = id;
  }
  
  /**
   * Get the reference ID
   * 
   * @return
   *   the reference id
   */
  public UniqueId getReferenceId()
  {
    return mReferenceId;
  }
  
  /**
   * Set the message priority
   * 
   * @param priority
   *   The message priority
   * @throws IllegalArgumentException
   *   if the new priority is invalid
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
   * @return
   *   the message priority
   */
  public int getPriority()
  {
    return mPriority;
  }

  /**
   * Set the request type.<br>
   * Note that this property must be set for administrative messages.
   * 
   * @param type
   *   The {@link ERequestType} of the message
   */
  public void setRequestType(ERequestType type)
  {
    mRequestType = type;
  }
  
  /**
   * Get the message's request type
   * 
   * @return
   *   the message's request type
   */
  public ERequestType getRequestType()
  {
    return mRequestType;
  }
  
  /**
   * Set the request's sub-type.<br>
   * Note that this property must be set for administrative messages.
   * 
   * @param subtype
   *   The {@link ERequestSubType} of the message
   */
  public void setRequestSubType(ERequestSubType subtype)
  {
    mRequestSubType = subtype;
  }
  
  /**
   * Get the message's request sub-type
   * 
   * @return
   *   the message's request sub-type
   */
  public ERequestSubType getRequestSubType()
  {
    return mRequestSubType;
  }
  
  /**
   * Get the timestamp at which the message was created
   * 
   * @return
   *   the timestamp at which the message was created
   */
  public long getTimeStamp()
  {
    return mTimeStamp;
  }
  
  /**
   * Set the number of milliseconds from message creation till the message expires
   * 
   * @param exp
   *   The number of milliseconds from message creation till the message expires
   */
  public void setExpiration(long exp)
  {
    mExpiration = exp;
  }
  
  /**
   * Get the number of milliseconds from message creation till the message expires
   * 
   * @return
   *   the number of milliseconds from message creation till the message expires
   */
  public long getExpiration()
  {
    return mExpiration;
  }
  
  /**
   * An indicator whether the message is expired
   * 
   * @return
   *   {@code true} if message expired, {@code false} otherwise
   */
  public boolean isExpired()
  {
    long millisFromCreation = System.currentTimeMillis() - mTimeStamp;
    return millisFromCreation > mExpiration;
  }
  
  /**
   * Set the {@link MqResponse}
   * 
   * @param resp
   *   The {@link MqResponse} to set
   */
  public void setResponse(MqResponse resp)
  {
    mResponse = resp;
  }
  
  /**
   * Get the {@link MqResponse}
   * 
   * @return
   *   the {@link MqResponse}
   */
  public MqResponse getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set a Object property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public void setObjectProperty(String key, Object value)
  {
    mProperties.setObjectProperty(key, value);
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
    return mProperties.getObjectProperty(key, defaultValue);
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
    mProperties.setBoolProperty(key, value);
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
    return mProperties.getBoolProperty(key, defaultValue);
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
    mProperties.setStringProperty(key, value);
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
    return mProperties.getStringProperty(key, defaultValue);
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
    mProperties.setByteProperty(key, value);
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
    return mProperties.getByteProperty(key, defaultValue);
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
    mProperties.setShortProperty(key, value);
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
    return mProperties.getShortProperty(key, defaultValue);
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
    mProperties.setIntProperty(key, value);
  }
  
  /**
   * Get an Integer property with default value if one is not present
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
    return mProperties.getIntProperty(key, defaultValue);
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
    mProperties.setLongProperty(key, value);
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
    return mProperties.getLongProperty(key, defaultValue);
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
    mProperties.setFloatProperty(key, value);
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
    return mProperties.getFloatProperty(key, defaultValue);
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
    mProperties.setDoubleProperty(key, value);
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
    return mProperties.getDoubleProperty(key, defaultValue);
  }
  
  /**
   * Get a subset of the properties from the message's properties
   * 
   * @param prefix
   *   A string that all returned properties are prefixed with
   * @return
   *   a subset of the properties collection
   */
  public Properties getSubset(String prefix)
  {
    return mProperties.getSubset(prefix);
  }
  
  /**
   * Include a subset of properties in the message's properties
   * 
   * @param props
   *   A subset of {@link Properties} to include
   * @return
   *   the number of properties included
   */
  public void setSubset(Properties props)
  {
    if (props != null)
      mProperties.putAll(props);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(pad).append("  Message Id=").append(mMessageId.toPrintableString()).append("\n")
      .append(pad).append("  Reference Id=").append(StringUtils.asPrintableString(mReferenceId)).append("\n")
      .append(pad).append("  Request Type=").append(StringUtils.asPrintableString(mRequestType)).append("\n")
      .append(pad).append("  Request SubType=").append(StringUtils.asPrintableString(mRequestSubType)).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append("  TimeStamp=").append(mTimeStamp).append("\n")
      .append(pad).append("  Expiration=").append(mExpiration).append("\n")
      .append(pad).append("  Response=").append(mResponse.toPrintableString(level+1)).append("\n")
      .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level+1)).append(")\n");
    return sb.toString();
  }
}
