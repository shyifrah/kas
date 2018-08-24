package com.kas.mq.impl;

import com.kas.comm.IPacket;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.mq.internal.ERequestType;

/**
 * The rules all {@link MqMessage} driven classes must obey
 * 
 * @author Pippo
 *
 * @param <T>
 */
public interface IMqMessage<T> extends IObject, IPacket
{
  /**
   * Get the message Id
   * 
   * @return the message id
   */
  public abstract UniqueId getMessageId();
  
  /**
   * Set the message priority
   * 
   * @param priority The message priority
   * 
   * @throws IllegalArgumentException if the new priority is invalid
   */
  public abstract void setPriority(int priority);
  
  /**
   * Get the message priority
   * 
   * @return the message priority
   */
  public abstract int getPriority();

  /**
   * Set the request type.<br>
   * <br>
   * Note that this property must be set for administrative messages.
   * 
   * @param type The {@link ERequestType} of the message
   */
  public abstract void setRequestType(ERequestType type);
  
  /**
   * Get the message's request type
   * 
   * @return the message's request type
   */
  public abstract ERequestType getRequestType();
  
  /**
   * Get the message's body
   * 
   * @return the message's body
   */
  public abstract T getBody();
  
  /**
   * Set the message's body
   * 
   * @param body The message's body
   */
  public abstract void setBody(T body);
  
  /**
   * Get a Object property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract Object getObjectProperty(String key, Object defaultValue);
  
  /**
   * Set a Object property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setObjectProperty(String key, Object value);
  
  /**
   * Get a Boolean property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract boolean getBoolProperty(String key, boolean defaultValue);
  
  /**
   * Set a Boolean property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setBoolProperty(String key, boolean value);
  
  /**
   * Get a String property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract String getStringProperty(String key, String defaultValue);
  
  /**
   * Set a String property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setStringProperty(String key, String value);
  
  /**
   * Get a Byte property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract byte getByteProperty(String key, byte defaultValue);
  
  /**
   * Set a Byte property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setByteProperty(String key, byte value);
  
  /**
   * Get a Short property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract short getShortProperty(String key, short defaultValue);
  
  /**
   * Set a Short property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setShortProperty(String key, short value);
  
  /**
   * Get an Integer property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract int getIntProperty(String key, int defaultValue);
  
  /**
   * Set an Integer property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setIntProperty(String key, int value);
  
  /**
   * Get a Long property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract long getLongProperty(String key, long defaultValue);
  
  /**
   * Set a Long property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setLongProperty(String key, long value);
  
  /**
   * Get a Float property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract float getFloatProperty(String key, float defaultValue);
  
  /**
   * Set a Float property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setFloatProperty(String key, float value);
  
  /**
   * Get a Double property with default value if one is not present
   * 
   * @param key The name of the property
   * @param defaultValue The default value of the property
   * @return the property value, or {@code defaultValue} if one is not present
   */
  public abstract double getDoubleProperty(String key, double defaultValue);
  
  /**
   * Set a Double property.
   * 
   * @param key The name of the property
   * @param value The value of the property
   */
  public abstract void setDoubleProperty(String key, double value);
}
