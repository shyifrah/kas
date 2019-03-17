package com.kas.mq.impl.messages;

import com.kas.comm.IPacket;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.internal.MqResponse;

/**
 * The rules a {@link MqMessage a base message} and its driven classes must obey
 * 
 * @author Pippo
 *
 * @param <T>
 */
public interface IMqMessage extends IObject, IPacket
{
  /**
   * Get the message ID
   * 
   * @return
   *   the message id
   */
  public abstract UniqueId getMessageId();
  
  /**
   * Set the reference ID
   * 
   * @param
   *   id The reference ID to set
   */
  public abstract void setReferenceId(UniqueId id);
  
  /**
   * Get the reference ID
   * 
   * @return
   *   the reference id
   */
  public abstract UniqueId getReferenceId();
  
  /**
   * Set the message priority
   * 
   * @param priority
   *   The message priority
   * @throws IllegalArgumentException
   *   if the new priority is invalid
   */
  public abstract void setPriority(int priority);
  
  /**
   * Get the message priority
   * 
   * @return
   *   the message priority
   */
  public abstract int getPriority();

  /**
   * Set the request type.<br>
   * Note that this property must be set for administrative messages.
   * 
   * @param type
   *   The {@link ERequestType} of the message
   */
  public abstract void setRequestType(ERequestType type);
  
  /**
   * Get the message's request type
   * 
   * @return
   *   the message's request type
   */
  public abstract ERequestType getRequestType();
  
  /**
   * Get the timestamp at which the message was created
   * 
   * @return
   *   the timestamp at which the message was created
   */
  public abstract long getTimeStamp();
  
  /**
   * Set the number of milliseconds from message creation till the message expires
   * 
   * @param exp
   *   The number of milliseconds from message creation till the message expires
   */
  public abstract void setExpiration(long exp);
  
  /**
   * Get the number of milliseconds from message creation till the message expires
   * 
   * @return
   *   the number of milliseconds from message creation till the message expires
   */
  public abstract long getExpiration();
  
  /**
   * An indicator whether the message is expired
   * 
   * @return
   *   {@code true} if message expired, {@code false} otherwise
   */
  public abstract boolean isExpired();
  
  /**
   * Set the {@link MqResponse}
   * 
   * @param resp
   *   The {@link MqResponse} to set
   */
  public abstract void setResponse(MqResponse resp);
  
  /**
   * Get the {@link MqResponse}
   * 
   * @return
   *   the {@link MqResponse}
   */
  public abstract MqResponse getResponse();
  
  /**
   * Set a Object property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setObjectProperty(String key, Object value);
  
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
  public abstract Object getObjectProperty(String key, Object defaultValue);
  
  /**
   * Set a Boolean property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setBoolProperty(String key, boolean value);
  
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
  public abstract boolean getBoolProperty(String key, boolean defaultValue);
  
  /**
   * Set a String property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setStringProperty(String key, String value);
  
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
  public abstract String getStringProperty(String key, String defaultValue);
  
  /**
   * Set a Byte property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setByteProperty(String key, byte value);
  
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
  public abstract byte getByteProperty(String key, byte defaultValue);
  
  /**
   * Set a Short property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setShortProperty(String key, short value);
  
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
  public abstract short getShortProperty(String key, short defaultValue);
  
  /**
   * Set an Integer property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setIntProperty(String key, int value);
  
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
  public abstract int getIntProperty(String key, int defaultValue);
  
  /**
   * Set a Long property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setLongProperty(String key, long value);
  
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
  public abstract long getLongProperty(String key, long defaultValue);
  
  /**
   * Set a Float property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setFloatProperty(String key, float value);
  
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
  public abstract float getFloatProperty(String key, float defaultValue);
  
  /**
   * Set a Double property.
   * 
   * @param key
   *   The name of the property
   * @param value
   *   The value of the property
   */
  public abstract void setDoubleProperty(String key, double value);
  
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
  public abstract double getDoubleProperty(String key, double defaultValue);
  
  /**
   * Get a subset of the properties from the message's properties
   * 
   * @param prefix
   *   A string that all returned properties are prefixed with
   * @return
   *   a subset of the properties collection
   */
  public abstract Properties getSubset(String prefix);
  
  /**
   * Include a subset of properties in the message's properties
   * 
   * @param props
   *   A subset of {@link Properties} to include
   * @return
   *   the number of properties included
   */
  public abstract void setSubset(Properties props);
}
