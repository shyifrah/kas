package com.kas.q.samples.internal;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.q.KasqMessage;

public class ReceiverThread extends AThread
{
  public ReceiverThread(Properties threadParams) throws KasException
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
      MessageConsumer consumer = session.createConsumer(queue);
      
      for (int i = 0; i < mNumOfMessages; i++)
      {
        Message msg = consumer.receive();
        if (msg instanceof KasqMessage)
        {
          KasqMessage kmsg = (KasqMessage)msg;
          System.out.println(kmsg.toPrintableString(0));
        }
      }
    }
    catch (JMSException e)
    {
      e.printStackTrace();
    }
  }
}
