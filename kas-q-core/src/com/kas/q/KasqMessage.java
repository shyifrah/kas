package com.kas.q;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.KasException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.KasqMessageHeader;
import com.kas.q.ext.EMessageType;
import com.kas.q.ext.KasqUtils;
import com.kas.q.ext.EReadWriteMode;
import com.kas.q.ext.IKasqConstants;

public class KasqMessage extends AKasObject implements IKasqMessage
{
  /***************************************************************************************************************
   * 
   */
  protected static ILogger sLogger = LoggerFactory.getLogger(KasqMessage.class);
  
  /***************************************************************************************************************
   * 
   */
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
  
  protected EReadWriteMode  mPropsMode = EReadWriteMode.cReadWrite;
  protected EReadWriteMode  mBodyMode  = EReadWriteMode.cReadWrite;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessage} object, specifying no parameters.<br>
   * No properties or headers are set, only the KAS/Q eye-catcher.
   */
  public KasqMessage()
  {
    mProperties.setBoolProperty(IKasqConstants.cKasqEyeCatcher, true);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream}
   * 
   * @throws IOException
   */
  public KasqMessage(ObjectInputStream istream) throws IOException
  {
    try
    {
      // headers
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
    catch (IOException e)
    {
      throw e;
    }
    catch (Throwable e)
    {
      throw new IOException(e);
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public void serialize(ObjectOutputStream ostream)
  {
    sLogger.diag("KasqMessage::serialize() - IN");
    
    try
    {
      // headers
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
      sLogger.diag("KasqMessage::serialize() - Exception caught: ", e);
      throw new RuntimeException(e);
    }
    
    sLogger.diag("KasqMessage::serialize() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
  public int getPacketClassId()
  {
    return PacketHeader.cClassIdKasq;
  }  
  
  /***************************************************************************************************************
   * 
   */
  public EMessageType getType()
  {
    return EMessageType.cKasqMessage;
  }  
  
  /***************************************************************************************************************
   * 
   */
  public PacketHeader createHeader()
  {
    return new KasqMessageHeader(getType().ordinal());
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean getBooleanProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getBoolProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getBoolProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setBooleanProperty(String key, boolean value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setBoolProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setBoolProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public byte getByteProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getByteProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getByteProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setByteProperty(String key, byte value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setByteProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setByteProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public double getDoubleProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getDoubleProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getDoubleProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setDoubleProperty(String key, double value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setDoubleProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setDoubleProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public float getFloatProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getFloatProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getFloatProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setFloatProperty(String key, float value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setFloatProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setFloatProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public int getIntProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getIntProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getIntProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setIntProperty(String key, int value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setIntProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setIntProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public long getLongProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getLongProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getLongProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setLongProperty(String key, long value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setLongProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setLongProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public Object getObjectProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getObjectProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getObjectProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setObjectProperty(String key, Object value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setObjectProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setObjectProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public short getShortProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getShortProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getShortProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setShortProperty(String key, short value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setShortProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setShortProperty(" + key + ", " + value + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public String getStringProperty(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.getStringProperty(key);
    }
    catch (KasException e)
    {
      throw new JMSException("Properties.getStringProperty(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void setStringProperty(String key, String value) throws JMSException
  {
    internalAssertPropertiesWriteable();
    try
    {
      mProperties.setStringProperty(key, value);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.setStringProperty(" + key + ", " + value + ") failed", e.getClass().getName());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public Enumeration<?> getPropertyNames() throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.keys();
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.keys() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public boolean propertyExists(String key) throws JMSException
  {
    internalAssertPropertiesReadable();
    try
    {
      return mProperties.containsKey(key);
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.containsKey(" + key + ") failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void acknowledge() throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Message.acknowledge()");
  }

  /***************************************************************************************************************
   * 
   */
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
      mPropsMode = EReadWriteMode.cReadWrite;
    }
    catch (Exception e)
    {
      throw new JMSException("Properties.clear() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   * 
   */
  public String getJMSCorrelationID() throws JMSException
  {
    try
    {
      return mCorrelationId.toString();
    }
    catch (Exception e)
    {
      throw new JMSException("return mCorrelationId.toString() failed", e.getMessage());
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
      throw new JMSException("mCorrelationId = " + correlationId, e.getMessage());
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
      throw new JMSException("mCorrelationId.getBytes()", e.getMessage());
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
      throw new JMSException("mCorrelationId = new String(" + correlationId + ")", e.getMessage());
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
      throw new JMSException("return mDeliveryMode", e.getMessage());
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
      throw new JMSException("mDeliveryMode = " + deliveryMode, e.getMessage());
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
      throw new JMSException("return mDeliveryTime", e.getMessage());
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
      throw new JMSException("mDeliveryTime = " + deliveryTime, e.getMessage());
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
      throw new JMSException("return mDestination", e.getMessage());
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
      throw new JMSException("mDestination = " + StringUtils.asString(destination), e.getMessage());
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
      throw new JMSException("return mExpiration", e.getMessage());
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
      throw new JMSException("mExpiration = " + expiration, e.getMessage());
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
      throw new JMSException("return mMessageId", e.getMessage());
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
      throw new JMSException("mMessageId = " + messageId, e.getMessage());
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
      throw new JMSException("return mPriority", e.getMessage());
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
      throw new JMSException("mPriority = " + priority, e.getMessage());
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
      throw new JMSException("return mRedelivered", e.getMessage());
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
      throw new JMSException("mRedelivered = " + redelivered, e.getMessage());
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
      throw new JMSException("return mReplyTo", e.getMessage());
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
      throw new JMSException("mReplyTo = " + StringUtils.asString(replyTo), e.getMessage());
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
      throw new JMSException("return mTimestamp", e.getMessage());
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
      throw new JMSException("mTimestamp = " + timestamp, e.getMessage());
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
      throw new JMSException("return mType", e.getMessage());
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
      throw new JMSException("mType = " + type, e.getMessage());
    }
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotReadableException} if message body is not in read-only or read-write mode
   * 
   * @throws JMSException if message body is not readable
   */
  protected void internalAssertBodyReadable() throws JMSException
  {
    if (mBodyMode == EReadWriteMode.cWriteOnly)
      throw new MessageNotReadableException("Message body is in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotReadableException} if message properties are not in read-only or read-write mode
   * 
   * @throws JMSException if message properties are not readable
   */
  protected void internalAssertPropertiesReadable() throws JMSException
  {
    if (mPropsMode == EReadWriteMode.cWriteOnly)
      throw new MessageNotReadableException("Message properties are in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotWriteableException} if message body is not in write-only or read-write mode
   * 
   * @throws JMSException if message body is not writeable
   */
  protected void internalAssertBodyWriteable() throws JMSException
  {
    if (mBodyMode == EReadWriteMode.cReadOnly)
      throw new MessageNotWriteableException("Message body is in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * Throws {@code MessageNotWriteableException} if message properties are not in write-only or read-write mode
   * 
   * @throws JMSException if message properties are not writeable
   */
  protected void internalAssertPropertiesWriteable() throws JMSException
  {
    if (mPropsMode == EReadWriteMode.cReadOnly)
      throw new MessageNotWriteableException("Message properties are in " + mBodyMode.toString() + " mode");
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toString()
  {
    return new StringBuffer()
      .append(name())
      .append("JMSMessageID=[")
      .append(StringUtils.asString(mMessageId))
      .append("],JMSTimestamp=[")
      .append(mTimestamp)
      .append("],JMSDestination=[")
      .append(StringUtils.asString(mDestination))
      .append("]")
      .toString();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  MessageId=").append(StringUtils.asString(mMessageId)).append("\n")
      .append(pad).append("  CorrelationId=").append(StringUtils.asString(mCorrelationId)).append("\n")
      .append(pad).append("  DeliveryMode=").append(KasqUtils.getFormattedDeliveryMode(mDeliveryMode)).append("\n")
      .append(pad).append("  DeliveryTime=").append(mDeliveryTime).append("\n")
      .append(pad).append("  Destination=").append(StringUtils.asString(mDestination)).append("\n")
      .append(pad).append("  ReplyTo=").append(StringUtils.asString(mReplyTo)).append("\n")
      .append(pad).append("  Redelivered=").append(mRedelivered).append("\n")
      .append(pad).append("  Expiration=").append(mExpiration).append("\n")
      .append(pad).append("  TimeStamp=").append(mTimestamp).append("\n")
      .append(pad).append("  Priority=").append(mPriority).append("\n")
      .append(pad).append("  Type=").append(mType).append("\n")
      .append(pad).append("  Properties=(").append(mProperties.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
