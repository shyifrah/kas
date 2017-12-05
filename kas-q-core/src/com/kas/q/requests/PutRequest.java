package com.kas.q.requests;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import com.kas.infra.base.UniqueId;
import com.kas.q.KasqMessageProducer;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public class PutRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  private KasqMessageProducer mProducer;
  private Destination         mDestination;
  private Message             mMessage;
  private int                 mDeliveryMode;
  private int                 mPriority;
  private long                mTimeToLive;
  
  /***************************************************************************************************************
   *  
   */
  public PutRequest(KasqMessageProducer producer, Destination destination, Message message, int deliveryMode, int priority, long timeToLive)
  {
    super();
    mProducer = producer;
    mDestination = destination;
    mMessage = message;
    mDeliveryMode = deliveryMode;
    mPriority = priority;
    mTimeToLive = timeToLive;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("PutRequest::setRequestProperties() - IN");
    
    // verify we are dealing with a KAS/Q message, otherwise - don't do anything
    boolean isKasqMessage = false;
    try
    {
      isKasqMessage = mMessage.getBooleanProperty(IKasqConstants.cKasqEyeCatcher);
    }
    catch (JMSException e) {}
    
    if (!isKasqMessage)
    {
      mLogger.debug("PutRequest::setRequestProperties() - Not a KAS/Q message, cannot continue with setup");
    }
    else
    {
      try
      {
        requestMessage = (IKasqMessage)mMessage;
        
        if (mProducer.getDisableMessageID())                                      // Message ID
          requestMessage.setJMSMessageID(null);                                   //   ..
        else                                                                      //     ..
          requestMessage.setJMSMessageID("ID:" + UniqueId.generate().toString()); //       ..
        
        long timestamp = System.currentTimeMillis();                              // Timestamp
        if (mProducer.getDisableMessageTimestamp())                               //   ..
          requestMessage.setJMSTimestamp(0);                                      //     ..
        else                                                                      //       ..
          requestMessage.setJMSTimestamp(timestamp);                              //         ..
        
        requestMessage.setJMSDestination(mDestination);                                   // Destination
        requestMessage.setJMSDeliveryMode(mDeliveryMode);                                 // Delivery Mode
        requestMessage.setJMSPriority(mPriority);                                         // Priority
        requestMessage.setJMSExpiration(mTimeToLive == 0 ? 0 : timestamp + mTimeToLive);  // Expiration
        requestMessage.setJMSDeliveryTime(timestamp + mProducer.getDeliveryDelay());      // Delivery Time
        
        requestMessage.setStringProperty(IKasqConstants.cPropertyProducerSession, mProducer.getSession().getSessionId());
        requestMessage.setLongProperty(IKasqConstants.cPropertyProducerDeliveryDelay, mProducer.getDeliveryDelay());
        requestMessage.setLongProperty(IKasqConstants.cPropertyProducerTimestamp, timestamp);
      }
      catch (Throwable e)
      {
        mLogger.debug("PutRequest::setRequestProperties() - JMSException caught: ", e);
      }
    }
    
    mLogger.debug("PutRequest::setRequestProperties() - OUT"); 
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cPut;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
