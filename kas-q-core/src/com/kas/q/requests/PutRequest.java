package com.kas.q.requests;

import javax.jms.Destination;
import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.KasqMessageProducer;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public class PutRequest extends ARequest
{
  private IKasqMessage mMessage;
  
  /***************************************************************************************************************
   *  
   */
  public PutRequest(KasqMessageProducer producer, Destination destination, IKasqMessage message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    super(ERequestType.cPut);
    
    if (producer.getDisableMessageID())                                         // Message ID
      message.setJMSMessageID(null);                                           //   ..
    else                                                                        //     ..
      message.setJMSMessageID("ID:" + UniqueId.generate().toString());         //       ..
    
    long timestamp = System.currentTimeMillis();                                // Timestamp
    if (producer.getDisableMessageTimestamp())                                  //   ..
      message.setJMSTimestamp(0);                                              //     ..
    else                                                                        //       ..
      message.setJMSTimestamp(timestamp);                                      //         ..
    
    message.setJMSDestination(destination);                                    // Destination
    message.setJMSDeliveryMode(deliveryMode);                                  // Delivery Mode
    message.setJMSPriority(priority);                                          // Priority
    message.setJMSExpiration(timeToLive == 0 ? 0 : timestamp + timeToLive);    // Expiration
    message.setJMSDeliveryTime(timestamp + producer.getDeliveryDelay());       // Delivery Time
    
    message.setStringProperty(IKasqConstants.cPropertyProducerSession, producer.getSession().getSessionId());
    message.setLongProperty(IKasqConstants.cPropertyProducerDeliveryDelay, producer.getDeliveryDelay());
    message.setLongProperty(IKasqConstants.cPropertyProducerTimestamp, timestamp);
    
    mMessage = message;
  }
  
  public IKasqMessage getMessage()
  {
    return mMessage;
  }
}
