package com.kas.q.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
//import javax.jms.Message;
import com.kas.infra.base.KasException;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.MessageType;
import com.kas.q.ext.impl.JmsUtils;

public class KasqMessage extends KasObject implements IMessage
{
  static enum ReadWriteMode {
    cReadOnly("read only"),
    cWriteOnly("write only"),
    cReadWrite("read/write");
    
    protected String mDesc;
    private ReadWriteMode(String desc)
    {
      mDesc = desc;
    }
    public String toString()
    {
      return mDesc;
    }
  };
  
  public static final String cKasQEyeCatcher = "KasQ_EyeCatcher";
  
  protected MessageType mMessageType   = MessageType.cMessage;
  
  protected String      mMessageId     = null;
  protected String      mCorrelationId = null;
  protected int         mDeliveryMode  = 0;
  protected long        mDeliveryTime  = 0L;
  protected Destination mDestination   = null;
  protected Destination mReplyTo       = null;
  protected boolean     mRedelivered   = false;
  protected long        mExpiration    = 0L;
  protected long        mTimestamp     = 0L;
  protected int         mPriority      = 0;
  protected String      mType          = null;
  protected Properties  mProperties    = new Properties();
  
  protected ReadWriteMode  mPropsMode = ReadWriteMode.cReadWrite;
  protected ReadWriteMode  mBodyMode  = ReadWriteMode.cReadWrite;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessage} object, specifying no parameters
   */
  public KasqMessage()
  {
    this(MessageType.cMessage);
    mProperties.setBoolProperty(cKasQEyeCatcher, true);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessage} object, specifying the message type
   */
  public KasqMessage(MessageType messageType)
  {
    mMessageType = messageType;
    mProperties.setBoolProperty(cKasQEyeCatcher, true);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream}
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqMessage(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    // headers
    mMessageType   = (MessageType)istream.readObject();
    mMessageId     = (String)istream.readObject();
    mCorrelationId = (String)istream.readObject();
    mDestination   = (Destination)istream.readObject();
    mReplyTo       = (Destination)istream.readObject();
    mDeliveryMode  = istream.readInt();
    mDeliveryTime  = istream.readLong();
    mRedelivered   = istream.readBoolean();
    mExpiration    = istream.readLong();
    mTimestamp     = istream.readLong();
    mPriority      = istream.readInt();
    mType          = (String)istream.readObject();
    
    // properties
    mProperties    = (Properties)istream.readObject();
  }
  
  /***************************************************************************************************************
   * Gets the message's type
   * 
   * @return The {@code MessageType} of the message
   */
  public MessageType getMessageType()
  {
    return mMessageType;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream)
  {
    try
    {
      // headers
      ostream.writeObject(mMessageType);
      ostream.reset();
      ostream.writeObject(mMessageId);
      ostream.reset();
      ostream.writeObject(mCorrelationId);
      ostream.reset();
      ostream.writeObject(mDestination);
      ostream.reset();
      ostream.writeObject(mReplyTo);
      ostream.reset();
      ostream.writeInt(mDeliveryMode);
      ostream.reset();
      ostream.writeLong(mDeliveryTime);
      ostream.reset();
      ostream.writeBoolean(mRedelivered);
      ostream.reset();
      ostream.writeLong(mExpiration);
      ostream.reset();
      ostream.writeLong(mTimestamp);
      ostream.reset();
      ostream.writeInt(mPriority);
      ostream.reset();
      ostream.writeObject(mType);
      ostream.reset();
      
      // properties
      ostream.writeObject(mProperties);
      ostream.reset();
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean getBooleanProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getBoolProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getBoolProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setBooleanProperty(String key, boolean value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setBoolProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setBoolProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public byte getByteProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getByteProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getByteProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setByteProperty(String key, byte value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setByteProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setByteProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public double getDoubleProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getDoubleProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getDoubleProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setDoubleProperty(String key, double value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setDoubleProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setDoubleProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public float getFloatProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getFloatProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getFloatProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setFloatProperty(String key, float value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setFloatProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setFloatProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public int getIntProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getIntProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getIntProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setIntProperty(String key, int value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setIntProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setIntProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public long getLongProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getLongProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getLongProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setLongProperty(String key, long value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setLongProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setLongProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public Object getObjectProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getObjectProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getObjectProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setObjectProperty(String key, Object value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setObjectProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setObjectProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public short getShortProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getShortProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getShortProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setShortProperty(String key, short value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setShortProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setShortProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public String getStringProperty(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.getStringProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.getStringProperty(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setStringProperty(String key, String value) throws JMSException
  {
    assertPropertiesWriteable();
    try
    {
      mProperties.setStringProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.setStringProperty(" + key + ", " + value + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public Enumeration<?> getPropertyNames() throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.keys();
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.keys()", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public boolean propertyExists(String key) throws JMSException
  {
    assertPropertiesReadable();
    try
    {
      return mProperties.containsKey(key);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.containsKey(" + key + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void acknowledge() throws JMSException
  {
    throw new JMSException("Unsupported method: Message.acknowledge()");
  }

  public void clearBody() throws JMSException
  {
  }

  /***************************************************************************************************************
   * 
   */
  public <T> T getBody(Class<T> c) throws JMSException
  {
    return null;
  }

  /***************************************************************************************************************
   * 
   */
  @SuppressWarnings("rawtypes")
  public boolean isBodyAssignableTo(Class c) throws JMSException
  {
    return true;
  }

  /***************************************************************************************************************
   * 
   */
  public void clearProperties() throws JMSException
  {
    try
    {
      mProperties.clear();
      mPropsMode = ReadWriteMode.cReadWrite;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Properties.clear()", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public String getJMSCorrelationID() throws JMSException
  {
    try
    {
      return mCorrelationId;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mCorrelationId", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSCorrelationID(String correlationId) throws JMSException
  {
    try
    {
      mCorrelationId = correlationId;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mCorrelationId = correlationId", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public byte[] getJMSCorrelationIDAsBytes() throws JMSException
  {
    try
    {
      return mCorrelationId.getBytes();
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mCorrelationId.getBytes()", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSCorrelationIDAsBytes(byte[] correlationId) throws JMSException
  {
    try
    {
      mCorrelationId = new String(correlationId);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mCorrelationId = new String(correlationId)", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public int getJMSDeliveryMode() throws JMSException
  {
    try
    {
      return mDeliveryMode;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mDeliveryMode", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSDeliveryMode(int deliveryMode) throws JMSException
  {
    try
    {
      mDeliveryMode = deliveryMode;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mDeliveryMode = deliveryMode", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public long getJMSDeliveryTime() throws JMSException
  {
    try
    {
      return mDeliveryTime;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mDeliveryTime", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSDeliveryTime(long deliveryTime) throws JMSException
  {
    try
    {
      mDeliveryTime = deliveryTime;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mDeliveryTime = deliveryTime", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public Destination getJMSDestination() throws JMSException
  {
    try
    {
      return mDestination;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mDestination", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSDestination(Destination destination) throws JMSException
  {
    try
    {
      mDestination = destination;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mDestination = destination", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public long getJMSExpiration() throws JMSException
  {
    try
    {
      return mExpiration;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mExpiration", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSExpiration(long expiration) throws JMSException
  {
    try
    {
      mExpiration = expiration;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mExpiration = expiration", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public String getJMSMessageID() throws JMSException
  {
    try
    {
      return mMessageId;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mMessageId", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSMessageID(String messageId) throws JMSException
  {
    try
    {
      mMessageId = messageId;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mMessageId = messageId", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public int getJMSPriority() throws JMSException
  {
    try
    {
      return mPriority;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mPriority", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSPriority(int priority) throws JMSException
  {
    try
    {
      mPriority = priority;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mPriority = priority", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public boolean getJMSRedelivered() throws JMSException
  {
    try
    {
      return mRedelivered;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mRedelivered", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSRedelivered(boolean redelivered) throws JMSException
  {
    try
    {
      mRedelivered = redelivered;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mRedelivered = redelivered", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public Destination getJMSReplyTo() throws JMSException
  {
    try
    {
      return mReplyTo;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mReplyTo", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSReplyTo(Destination replyTo) throws JMSException
  {
    try
    {
      mReplyTo = replyTo;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mReplyTo = replyTo", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public long getJMSTimestamp() throws JMSException
  {
    try
    {
      return mTimestamp;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mTimestamp", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSTimestamp(long timestamp) throws JMSException
  {
    try
    {
      mTimestamp = timestamp;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mTimestamp = timestamp", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public String getJMSType() throws JMSException
  {
    try
    {
      return mType;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "return mType", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setJMSType(String type) throws JMSException
  {
    try
    {
      mType = type;
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "mType = type", e);
    }
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotReadableException} if message body is not in read-only or read-write mode
   * 
   * @throws JMSException if message body is not readable
   */
  protected void assertBodyReadable() throws JMSException
  {
    if (mBodyMode == ReadWriteMode.cWriteOnly)
      throw new MessageNotReadableException("Message body is in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotReadableException} if message properties are not in read-only or read-write mode
   * 
   * @throws JMSException if message properties are not readable
   */
  protected void assertPropertiesReadable() throws JMSException
  {
    if (mPropsMode == ReadWriteMode.cWriteOnly)
      throw new MessageNotReadableException("Message properties are in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotWriteableException} if message body is not in write-only or read-write mode
   * 
   * @throws JMSException if message body is not writeable
   */
  protected void assertBodyWriteable() throws JMSException
  {
    if (mBodyMode == ReadWriteMode.cReadOnly)
      throw new MessageNotWriteableException("Message body is in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotWriteableException} if message properties are not in write-only or read-write mode
   * 
   * @throws JMSException if message properties are not writeable
   */
  protected void assertPropertiesWriteable() throws JMSException
  {
    if (mPropsMode == ReadWriteMode.cReadOnly)
      throw new MessageNotWriteableException("Message properties are in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  MessageId=").append(mMessageId).append("\n")
      .append(pad).append("  CorrelationId=").append(mCorrelationId).append("\n")
      .append(pad).append("  DeliveryMode=").append(JmsUtils.toString(mDeliveryMode)).append("\n")
      .append(pad).append("  DeliveryTime=").append(mDeliveryTime).append("\n")
      .append(pad).append("  Destination=").append(StringUtils.asString(mDestination)).append("\n")
      .append(pad).append("  ReplyTo=").append(StringUtils.asString(mReplyTo)).append("\n")
      .append(pad).append("  Redelivered=").append(mRedelivered).append("\n")
      .append(pad).append("  Expiration=").append(mExpiration).append("\n")
      .append(pad).append("  TimeStamp=").append(mTimestamp).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append("  Type=").append(mType).append("\n")
      .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toString()
  {
    return new StringBuffer()
      .append(name())
      .append("UniqueID:")
      .append(mMessageId == null ? "null" : mMessageId.toString())
      .toString();
  }
}
