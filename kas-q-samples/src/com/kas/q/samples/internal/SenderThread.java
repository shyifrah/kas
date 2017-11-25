package com.kas.q.samples.internal;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;

public class SenderThread extends AThread
{
  public SenderThread(Properties threadParams) throws KasException
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
        String text = "shyifrah-" + Integer.toString(i);
        System.out.println("Sending message " + i + " to queue: " + mQueue.getName());
        TextMessage msg = mSession.createTextMessage(text);
        producer.send(msg);
      }
    }
    catch (JMSException e)
    {
      e.printStackTrace();
    }
  }
}
