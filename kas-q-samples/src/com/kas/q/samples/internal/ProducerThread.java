package com.kas.q.samples.internal;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.RunTimeUtils;

public class ProducerThread extends AThread
{
  public static final String cProperty_SendDelay = "send_delay"; 
  
  public ProducerThread(Properties threadParams) throws KasException
  {
    super(threadParams);
  }
  
  public void work()
  {
    try
    {
      MessageProducer producer = mSession.createProducer(mQueue);
      
      for (int i = 1; i <= mNumOfMessages; i++)
      {
        sendOneMessage(producer, i);
      }
    }
    catch (JMSException e)
    {
      e.printStackTrace();
    }
  }
  
  private void sendOneMessage(MessageProducer producer, int i) throws JMSException
  {
    int sendDelay = mProperties.getIntProperty(cProperty_SendDelay, 0);
    
    String text = "shyifrah-" + i;
    TextMessage msg = mSession.createTextMessage(text);
    
    if (sendDelay > 0)
      RunTimeUtils.sleep(sendDelay);
    
    producer.send(msg);
    
    System.out.println("Sending message " + i + ": " + msg.toString());
  }
}
