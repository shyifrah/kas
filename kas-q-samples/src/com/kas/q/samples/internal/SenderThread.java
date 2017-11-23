package com.kas.q.samples.internal;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
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
      Session session = mConnection.createSession();
      String qname = mProperties.getStringProperty(AThread.cProperty_QueueName, "default.queue.name");
      Queue  queue = session.createQueue(qname);
      MessageProducer producer = session.createProducer(queue);
      
      for (int i = 0; i < mNumOfMessages; i++)
      {
        String text = "shyifrah-" + Integer.toString(i);
        TextMessage msg = session.createTextMessage(text);
        producer.send(msg);
      }
    }
    catch (JMSException e)
    {
      e.printStackTrace();
    }
  }
}
