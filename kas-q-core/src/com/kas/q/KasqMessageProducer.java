package com.kas.q;

import javax.jms.CompletionListener;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;

public class KasqMessageProducer extends AKasObject implements MessageProducer
{
  private static ILogger sLogger = LoggerFactory.getLogger(KasqMessageProducer.class);  
  
  protected KasqSession mSession = null;
  protected IKasqDestination mDestination = null;
  private boolean mDisableMessageId = false;
  private boolean mDisableMessageTimestamp = false;
  private int     mDeliveryMode = DeliveryMode.PERSISTENT;
  private int     mPriority = 0;
  private long    mTimeToLive = 0;
  private long    mDeliveryDelay = 0;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageProducer} object with a default {@code Destination}
   * 
   * @param session the session associated with this {@code MessageProducer}
   * @param destination a default destination associated with the {@code MessageProducer}
   */
  KasqMessageProducer(KasqSession session, Destination destination) throws JMSException
  {
    if ((destination != null) && (!(destination instanceof IKasqDestination)))
      throw new InvalidDestinationException("Unsupported destination", "Destination is not managed by KAS/Q");
    
    mSession = session;
    mDestination = (IKasqDestination)destination;
  }
  
  /***************************************************************************************************************
   * 
   */
  public Destination getDestination()
  {
    return mDestination;
  }

  /***************************************************************************************************************
   * 
   */
  public boolean getDisableMessageID() throws JMSException
  {
    return mDisableMessageId;
  }

  /***************************************************************************************************************
   * 
   */
  public void setDisableMessageID(boolean value) throws JMSException
  {
    mDisableMessageId = value;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean getDisableMessageTimestamp() throws JMSException
  {
    return mDisableMessageTimestamp;
  }

  /***************************************************************************************************************
   * 
   */
  public void setDisableMessageTimestamp(boolean value) throws JMSException
  {
    mDisableMessageTimestamp = value;
  }
  
  /***************************************************************************************************************
   * 
   */
  public int getDeliveryMode() throws JMSException
  {
    return mDeliveryMode;
  }

  /***************************************************************************************************************
   * 
   */
  public void setDeliveryMode(int deliveryMode) throws JMSException
  {
    if ((deliveryMode != DeliveryMode.PERSISTENT) && (deliveryMode != DeliveryMode.NON_PERSISTENT))
      throw new JMSException("Invalid DeliveryMode: " + deliveryMode);
      
    mDeliveryMode = deliveryMode;
  }

  /***************************************************************************************************************
   * 
   */
  public int getPriority() throws JMSException
  {
    return mPriority;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void setPriority(int defaultPriority) throws JMSException
  {
    if ((defaultPriority < 0) || (defaultPriority > 9))
      throw new JMSException("Invalid Priority: " + defaultPriority);
      
    mPriority = defaultPriority;
  }
  
  /***************************************************************************************************************
   * 
   */
  public long getTimeToLive() throws JMSException
  {
    return mTimeToLive;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void setTimeToLive(long timeToLive) throws JMSException
  {
    if (timeToLive < 0)
      throw new JMSException("Invalid Time-to-Live: " + timeToLive);
    
    mTimeToLive = timeToLive;
  }
  
  /***************************************************************************************************************
   * 
   */
  public long getDeliveryDelay() throws JMSException
  {
    return mDeliveryDelay;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void setDeliveryDelay(long deliveryDelay) throws JMSException
  {
    if (deliveryDelay < 0)
      throw new JMSException("Invalid Delivery delay: " + deliveryDelay);
    
    mDeliveryDelay = deliveryDelay;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void close() throws JMSException
  {
    // TODO: need to do something?
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Message message) throws JMSException
  {
    send(mDestination, message, mDeliveryMode, mPriority, mTimeToLive);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Destination destination, Message message) throws JMSException
  {
    send(destination, message, mDeliveryMode, mPriority, mTimeToLive);
  }
  
  /***************************************************************************************************************
   * 
   */
  public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(mDestination, message, deliveryMode, priority, timeToLive);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(destination, message, deliveryMode, priority, timeToLive, null);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Message message, CompletionListener completionListener) throws JMSException
  {
    send(mDestination, message, mDeliveryMode, mPriority, mTimeToLive, completionListener);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException
  {
    send(mDestination, message, deliveryMode, priority, timeToLive, completionListener);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Destination destination, Message message, CompletionListener completionListener) throws JMSException
  {
    send(destination, message, mDeliveryMode, mPriority, mTimeToLive, completionListener);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException
  {
    try
    {
      internalSetup(destination, message, deliveryMode, priority, timeToLive);
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
   * Admin messages are processed on the spot by the KAS/Q server, so we allow null destination
   * 
   * @param destination message destination
   * @param message message to be sent
   * @param deliveryMode message delivery's mode
   * @param priority message priority
   * @param timeToLive milliseconds until message can be discarded by provider
   * 
   * @throws JMSException
   */
  private void internalSetup(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    sLogger.debug("KasqMessageProducer::internalSetup() - IN");
    
    if (message == null)
      throw new JMSException("Cannot send a null message");
    
    boolean admin = false;
    try
    {
      admin = message.getBooleanProperty(IKasqConstants.cPropertyAdminMessage);
    }
    catch (Throwable e) {}
    
    if (admin)
    {
      sLogger.debug("KasqMessageProducer::internalSetup() - Priviliged message, skipping destination verification");
    }
    else
    {
      sLogger.debug("KasqMessageProducer::internalSetup() - Verifying destination is not NULL");
      if (destination == null)
        throw new JMSException("Cannot send message. Null destination");
    }
    
    if ((deliveryMode != DeliveryMode.PERSISTENT) && (deliveryMode != DeliveryMode.NON_PERSISTENT))
      throw new JMSException("Invalid DeliveryMode: " + deliveryMode);
    
    if ((priority < 0) || (priority > 9))
      throw new JMSException("Invalid Priority: " + priority);
    
    if (timeToLive < 0)
      throw new JMSException("Invalid Time-to-Live: " + timeToLive);
    
    sLogger.debug("KasqMessageProducer::internalSetup() - OUT");
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
    sLogger.debug("KasqMessageProducer::internalSend() - IN");
    
    String messageId = null;
    if (!mDisableMessageId)
      messageId = UniqueId.generate().toString();
    message.setJMSMessageID(messageId);
    
    long timestamp = System.currentTimeMillis();
    if (!mDisableMessageTimestamp)
      message.setJMSTimestamp(timestamp);
    
    message.setJMSDestination(destination);
    message.setJMSDeliveryMode(deliveryMode);
    message.setJMSPriority(priority);
    message.setJMSExpiration(timeToLive);
    
    boolean eyeCatcher = message.getBooleanProperty(IKasqConstants.cKasqEyeCatcher);
    if (eyeCatcher)
    {
      IKasqMessage iMessage = (IKasqMessage)message;
      
      iMessage.setStringProperty(IKasqConstants.cPropertyProducerSession, mSession.getSessionId());
      iMessage.setLongProperty(IKasqConstants.cPropertyProducerDeliveryDelay, mDeliveryDelay);
      iMessage.setLongProperty(IKasqConstants.cPropertyProducerTimestamp, timestamp);
      
      sLogger.debug("KasqMessageProducer::internalSend() - Sending message: " + iMessage.toPrintableString(0));
      mSession.internalSend(iMessage);
    }
    else
    {
      sLogger.debug("KasqMessageProducer::internalSend() - Not a KAS/Q message, cannot send");      
    }
    
    sLogger.debug("KasqMessageProducer::internalSend() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
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
