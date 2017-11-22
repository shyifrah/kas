package com.kas.q.samples;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import com.kas.q.KasqMessage;

public class ReceiverThread extends AThread
{
  ReceiverThread(String name, int numOfMessages, int delay, Session session, Queue queue)
  {
    super(name, numOfMessages, delay, session, queue);
  }
  
  public void work()
  {
    try
    {
      MessageConsumer consumer = mSession.createConsumer(mQueue);
      
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
