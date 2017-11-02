package com.kas.q.impl;

import javax.jms.CompletionListener;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.IMessage;

public class KasqMessageProducer extends KasObject implements MessageProducer
{
  private KasqSession mSession = null;
  private Destination mDestination = null;
  private boolean     mDisableMessageId = false;
  private boolean     mDisableMessageTimestamp = false;
  private int         mDeliveryMode = DeliveryMode.PERSISTENT;
  private int         mPriority = 0;
  private long        mTimeToLive = 0;
  private long        mDeliveryDelay = 0;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageProducer} object with a default {@code Destination}
   * 
   * @param session the session associated with this {@code MessageProducer}
   * @param destination a default destination associated with the {@code MessageProducer}
   */
  KasqMessageProducer(KasqSession session, Destination destination)
  {
    mSession = session;
    mDestination = destination;
  }
  
  public Destination getDestination()
  {
    return mDestination;
  }

  public boolean getDisableMessageID() throws JMSException
  {
    return mDisableMessageId;
  }

  public void setDisableMessageID(boolean value) throws JMSException
  {
    mDisableMessageId = value;
  }
  
  public boolean getDisableMessageTimestamp() throws JMSException
  {
    return mDisableMessageTimestamp;
  }

  public void setDisableMessageTimestamp(boolean value) throws JMSException
  {
    mDisableMessageTimestamp = value;
  }
  
  public int getDeliveryMode() throws JMSException
  {
    return mDeliveryMode;
  }

  public void setDeliveryMode(int deliveryMode) throws JMSException
  {
    if ((deliveryMode != DeliveryMode.PERSISTENT) && (deliveryMode != DeliveryMode.NON_PERSISTENT))
      throw new JMSException("Invalid DeliveryMode: " + deliveryMode);
      
    mDeliveryMode = deliveryMode;
  }

  public int getPriority() throws JMSException
  {
    return mPriority;
  }
  
  public void setPriority(int defaultPriority) throws JMSException
  {
    if ((defaultPriority < 0) || (defaultPriority > 9))
      throw new JMSException("Invalid Priority: " + defaultPriority);
      
    mPriority = defaultPriority;
  }
  
  public long getTimeToLive() throws JMSException
  {
    return mTimeToLive;
  }
  
  public void setTimeToLive(long timeToLive) throws JMSException
  {
    if (timeToLive < 0)
      throw new JMSException("Invalid Time-to-Live: " + timeToLive);
    
    mTimeToLive = timeToLive;
  }
  
  public long getDeliveryDelay() throws JMSException
  {
    return mDeliveryDelay;
  }
  
  public void setDeliveryDelay(long deliveryDelay) throws JMSException
  {
    if (deliveryDelay < 0)
      throw new JMSException("Invalid Delivery delay: " + deliveryDelay);
    
    mDeliveryDelay = deliveryDelay;
  }
  
  public void close() throws JMSException
  {
    //
    //
    //
  }

  public void send(Message message) throws JMSException
  {
    send(mDestination, message, mDeliveryMode, mPriority, mTimeToLive);
  }

  public void send(Destination destination, Message message) throws JMSException
  {
    send(destination, message, mDeliveryMode, mPriority, mTimeToLive);
  }
  
  public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(mDestination, message, deliveryMode, priority, timeToLive);
  }

  public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(destination, message, deliveryMode, priority, timeToLive, null);
  }

  public void send(Message message, CompletionListener completionListener) throws JMSException
  {
    send(mDestination, message, mDeliveryMode, mPriority, mTimeToLive, completionListener);
  }

  public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException
  {
    send(mDestination, message, deliveryMode, priority, timeToLive, completionListener);
  }

  public void send(Destination destination, Message message, CompletionListener completionListener) throws JMSException
  {
    send(destination, message, mDeliveryMode, mPriority, mTimeToLive, completionListener);
  }

  public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException
  {
    try
    {
      internalVerify(destination, message, deliveryMode, priority, timeToLive);
      internalSend(destination, message, deliveryMode, priority, timeToLive);
      
      if (completionListener != null)
      {
        completionListener.onCompletion(message);
      }
    }
    catch (JMSException e)
    {
      if (completionListener != null)
      {
        completionListener.onException(message, e);
      }
      else
      {
        throw e;
      }
    }
  }

  /***************************************************************************************************************
   * Verifies the validity of message headers prior to send operation
   * 
   * @param destination message destination
   * @param message message to be sent
   * @param deliveryMode message delivery's mode
   * @param priority message priority
   * @param timeToLive milliseconds until message can be discarded by provider
   * 
   * @throws JMSException
   */
  private void internalVerify(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    if (destination == null)
      throw new JMSException("Cannot send message. Null destination");
    
    if (message == null)
      throw new JMSException("Cannot send a null message");
    
    if ((deliveryMode != DeliveryMode.PERSISTENT) && (deliveryMode != DeliveryMode.NON_PERSISTENT))
      throw new JMSException("Invalid DeliveryMode: " + deliveryMode);
    
    if ((priority < 0) || (priority > 9))
      throw new JMSException("Invalid Priority: " + priority);
    
    if (timeToLive < 0)
      throw new JMSException("Invalid Time-to-Live: " + timeToLive);
  }
  
  /***************************************************************************************************************
   * Send a {@code Message}
   * 
   * @param destination message destination
   * @param message message to be sent
   * @param deliveryMode message delivery's mode
   * @param priority message priority
   * @param timeToLive milliseconds until message can be discarded by provider
   * 
   * @throws JMSException
   */
  private void internalSend(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    try
    {
      String messageId = null;
      if (!mDisableMessageId)
        messageId = new UniqueId().toString();
      message.setJMSMessageID(messageId);
      
      long timestamp = System.currentTimeMillis();
      if (!mDisableMessageTimestamp)
        message.setJMSTimestamp(timestamp);
      
      message.setJMSDestination(destination);
      message.setJMSPriority(priority);
      message.setJMSDeliveryMode(deliveryMode);
      message.setJMSExpiration(timeToLive);
      
      boolean eyeCatcher = message.getBooleanProperty(KasqMessage.cKasQEyeCatcher);
      if (eyeCatcher)
      {
        IMessage iMessage = (IMessage)message;
        mSession.mConnection.send(iMessage);
      }
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("Error while sending message", "Exception caught: " + e.getClass().getName(), e);
    }
  }
  
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Destination=").append(mDestination).append("\n")
      .append(pad).append("  MessageId disabled=").append(mDisableMessageId).append("\n")
      .append(pad).append("  Timestamp disabled=").append(mDisableMessageTimestamp).append("\n")
      .append(pad).append("  Default DeliveryMode=").append(mDeliveryMode).append("\n")
      .append(pad).append("  Default Priority=").append(mPriority).append("\n")
      .append(pad).append("  Default TTL=").append(mTimeToLive).append("\n")
      .append(pad).append("  Default DeliveryDelay=").append(mDeliveryDelay).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
