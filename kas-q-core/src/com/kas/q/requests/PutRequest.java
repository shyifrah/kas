package com.kas.q.requests;

import javax.jms.Destination;
import javax.jms.JMSException;
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
  private int                 mDeliveryMode;
  private int                 mPriority;
  private long                mTimeToLive;
  
  /***************************************************************************************************************
   *  
   */
  public PutRequest(KasqMessageProducer producer, Destination destination, IKasqMessage message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    super(ERequestType.cPut, message);
    mProducer = producer;
    mDestination = destination;
    mDeliveryMode = deliveryMode;
    mPriority = priority;
    mTimeToLive = timeToLive;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setup()
  {
    mLogger.debug("PutRequest::setup() - IN");
    
    try
    {
      if (mProducer.getDisableMessageID())                                        // Message ID
        mMessage.setJMSMessageID(null);                                           //   ..
      else                                                                        //     ..
        mMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());         //       ..
      
      long timestamp = System.currentTimeMillis();                                // Timestamp
      if (mProducer.getDisableMessageTimestamp())                                 //   ..
        mMessage.setJMSTimestamp(0);                                              //     ..
      else                                                                        //       ..
        mMessage.setJMSTimestamp(timestamp);                                      //         ..
      
      mMessage.setJMSDestination(mDestination);                                   // Destination
      mMessage.setJMSDeliveryMode(mDeliveryMode);                                 // Delivery Mode
      mMessage.setJMSPriority(mPriority);                                         // Priority
      mMessage.setJMSExpiration(mTimeToLive == 0 ? 0 : timestamp + mTimeToLive);  // Expiration
      mMessage.setJMSDeliveryTime(timestamp + mProducer.getDeliveryDelay());      // Delivery Time
      
      mMessage.setStringProperty(IKasqConstants.cPropertyProducerSession, mProducer.getSession().getSessionId());
      mMessage.setLongProperty(IKasqConstants.cPropertyProducerDeliveryDelay, mProducer.getDeliveryDelay());
      mMessage.setLongProperty(IKasqConstants.cPropertyProducerTimestamp, timestamp);
    }
    catch (Throwable e)
    {
      mLogger.debug("PutRequest::setup() - JMSException caught: ", e);
    }
    
    mLogger.debug("PutRequest::setup() - OUT"); 
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
