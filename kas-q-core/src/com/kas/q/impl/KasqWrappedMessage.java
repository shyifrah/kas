package com.kas.q.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import com.kas.q.ext.MessageType;
import com.kas.q.ext.impl.JmsUtils;

public class KasqWrappedMessage extends KasqMessage
{
  protected Message mJmsMessage;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqWrappedMessage} object, specifying no parameters
   * 
   * @param message the {@link javax.jms.Message}
   */
  KasqWrappedMessage(Message message)
  {
    mJmsMessage = message;
    mMessageType = MessageType.cJavaxJmsMessage;
    mProperties.setBoolProperty(cKasQEyeCatcher, true);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqWrappedMessage} object, specifying the message type
   * 
   * @param messageType the {@code MessageType}
   */
  KasqWrappedMessage(MessageType messageType)
  {
    mMessageType = messageType;
    mProperties.setBoolProperty(cKasQEyeCatcher, true);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqWrappedMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqWrappedMessage(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    mMessageType = MessageType.cJavaxJmsMessage;
    mJmsMessage = (Message)istream.readObject();
  }
  
  public MessageType getMessageType()
  {
    return mMessageType;
  }
  
  public void serialize(ObjectOutputStream ostream)
  {
    try
    {
      ostream.writeObject(mJmsMessage);
      ostream.reset();
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public boolean getBooleanProperty(String key) throws JMSException
  {
    return mJmsMessage.getBooleanProperty(key);
  }

  public void setBooleanProperty(String key, boolean value) throws JMSException
  {
    mJmsMessage.setBooleanProperty(key, value);
  }

  public byte getByteProperty(String key) throws JMSException
  {
    return mJmsMessage.getByteProperty(key);
  }

  public void setByteProperty(String key, byte value) throws JMSException
  {
    mJmsMessage.setByteProperty(key, value);
  }

  public double getDoubleProperty(String key) throws JMSException
  {
    return mJmsMessage.getDoubleProperty(key);
  }

  public void setDoubleProperty(String key, double value) throws JMSException
  {
    mJmsMessage.setDoubleProperty(key, value);
  }

  public float getFloatProperty(String key) throws JMSException
  {
    return mJmsMessage.getFloatProperty(key);
  }

  public void setFloatProperty(String key, float value) throws JMSException
  {
    mJmsMessage.setFloatProperty(key, value);
  }

  public int getIntProperty(String key) throws JMSException
  {
    return mJmsMessage.getIntProperty(key);
  }

  public void setIntProperty(String key, int value) throws JMSException
  {
    mJmsMessage.setIntProperty(key, value);
  }

  public long getLongProperty(String key) throws JMSException
  {
    return mJmsMessage.getLongProperty(key);
  }

  public void setLongProperty(String key, long value) throws JMSException
  {
    mJmsMessage.setLongProperty(key, value);
  }

  public Object getObjectProperty(String key) throws JMSException
  {
    return mJmsMessage.getObjectProperty(key);
  }

  public void setObjectProperty(String key, Object value) throws JMSException
  {
    mJmsMessage.setObjectProperty(key, value);
  }

  public short getShortProperty(String key) throws JMSException
  {
    return mJmsMessage.getShortProperty(key);
  }

  public void setShortProperty(String key, short value) throws JMSException
  {
    mJmsMessage.setShortProperty(key, value);
  }

  public String getStringProperty(String key) throws JMSException
  {
    return mJmsMessage.getStringProperty(key);
  }

  public void setStringProperty(String key, String value) throws JMSException
  {
    mJmsMessage.setStringProperty(key, value);
  }

  public Enumeration<?> getPropertyNames() throws JMSException
  {
    return mJmsMessage.getPropertyNames();
  }

  public boolean propertyExists(String key) throws JMSException
  {
    return mJmsMessage.propertyExists(key);
  }

  public void acknowledge() throws JMSException
  {
    throw new JMSException("Unsupported method: Message.acknowledge()");
  }

  public void clearBody() throws JMSException
  {
    mJmsMessage.clearBody();
  }

  public <T> T getBody(Class<T> c) throws JMSException
  {
    return mJmsMessage.getBody(c);
  }

  @SuppressWarnings("rawtypes")
  public boolean isBodyAssignableTo(Class c) throws JMSException
  {
    return mJmsMessage.isBodyAssignableTo(c);
  }

  public void clearProperties() throws JMSException
  {
    mJmsMessage.clearBody();
  }

  public String getJMSCorrelationID() throws JMSException
  {
    return mJmsMessage.getJMSCorrelationID();
  }

  public void setJMSCorrelationID(String correlationId) throws JMSException
  {
    mJmsMessage.setJMSCorrelationID(correlationId);
  }

  public byte[] getJMSCorrelationIDAsBytes() throws JMSException
  {
    return mJmsMessage.getJMSCorrelationIDAsBytes();
  }

  public void setJMSCorrelationIDAsBytes(byte[] correlationId) throws JMSException
  {
    mJmsMessage.setJMSCorrelationIDAsBytes(correlationId);
  }

  public int getJMSDeliveryMode() throws JMSException
  {
    return mJmsMessage.getJMSDeliveryMode();
  }

  public void setJMSDeliveryMode(int deliveryMode) throws JMSException
  {
    mJmsMessage.setJMSDeliveryMode(deliveryMode);
  }

  public long getJMSDeliveryTime() throws JMSException
  {
    return mJmsMessage.getJMSDeliveryTime();
  }

  public void setJMSDeliveryTime(long deliveryTime) throws JMSException
  {
    mJmsMessage.setJMSDeliveryTime(deliveryTime);
  }

  public Destination getJMSDestination() throws JMSException
  {
    return mJmsMessage.getJMSDestination();
  }

  public void setJMSDestination(Destination destination) throws JMSException
  {
    mJmsMessage.setJMSDestination(destination);
  }

  public long getJMSExpiration() throws JMSException
  {
    return mJmsMessage.getJMSExpiration();
  }

  public void setJMSExpiration(long expiration) throws JMSException
  {
    mJmsMessage.setJMSExpiration(expiration);
  }

  public String getJMSMessageID() throws JMSException
  {
    return mJmsMessage.getJMSMessageID();
  }

  public void setJMSMessageID(String messageId) throws JMSException
  {
    mJmsMessage.setJMSMessageID(messageId);
  }

  public int getJMSPriority() throws JMSException
  {
    return mJmsMessage.getJMSPriority();
  }

  public void setJMSPriority(int priority) throws JMSException
  {
    mJmsMessage.setJMSPriority(priority);
  }

  public boolean getJMSRedelivered() throws JMSException
  {
    return mJmsMessage.getJMSRedelivered();
  }

  public void setJMSRedelivered(boolean redelivered) throws JMSException
  {
    mJmsMessage.setJMSRedelivered(redelivered);
  }

  public Destination getJMSReplyTo() throws JMSException
  {
    return mJmsMessage.getJMSReplyTo();
  }

  public void setJMSReplyTo(Destination replyTo) throws JMSException
  {
    mJmsMessage.setJMSReplyTo(replyTo);
  }

  public long getJMSTimestamp() throws JMSException
  {
    return mJmsMessage.getJMSTimestamp();
  }

  public void setJMSTimestamp(long timestamp) throws JMSException
  {
    mJmsMessage.setJMSTimestamp(timestamp);
  }

  public String getJMSType() throws JMSException
  {
    return mJmsMessage.getJMSType();
  }

  public void setJMSType(String type) throws JMSException
  {
    mJmsMessage.setJMSType(type);
  }
  
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name());
    
    try
    {
      sb.append("(\n")
        .append(pad).append("  MessageId=").append(getJMSMessageID()).append("\n")
        .append(pad).append("  CorrelationId=").append(getJMSCorrelationID()).append("\n")
        .append(pad).append("  DeliveryMode=").append(JmsUtils.toString(getJMSDeliveryMode())).append("\n")
        .append(pad).append("  DeliveryTime=").append(getJMSDeliveryTime()).append("\n")
        .append(pad).append("  Destination=").append(getJMSDestination().toString()).append("\n")
        .append(pad).append("  ReplyTo=").append(getJMSReplyTo().toString()).append("\n")
        .append(pad).append("  Redelivered=").append(getJMSRedelivered()).append("\n")
        .append(pad).append("  Expiration=").append(getJMSExpiration()).append("\n")
        .append(pad).append("  TimeStamp=").append(getJMSTimestamp()).append("\n")
        .append(pad).append("  Priority=").append(getJMSPriority()).append("\n")
        .append(pad).append("  Type=").append(getJMSType()).append("\n")
        .append(pad).append(")");
    }
    catch (Throwable e) {}
    
    return sb.toString();
  }
  
  public String toString()
  {
    return new StringBuffer()
      .append(name())
      .append("UniqueID:")
      .append(mMessageId == null ? "null" : mMessageId.toString())
      .toString();
  }
}
