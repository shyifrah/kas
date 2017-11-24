package com.kas.q.samples.internal;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
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
      ConnectionFactory factory = mClient.getFactory();
      Connection conn = factory.createConnection();
      Session session = conn.createSession();
      String qname = mProperties.getStringProperty(AThread.cProperty_QueueName, "default.queue.name");
      
      Queue queue = mClient.locateQueue(qname);
      if (queue == null)
        queue = session.createQueue(qname);
      MessageProducer producer = session.createProducer(queue);
      
      for (int i = 1; i <= mNumOfMessages; i++)
      {
        String text = "shyifrah-" + Integer.toString(i);
        System.out.println("Sending message " + i + " to queue: " + qname);
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
