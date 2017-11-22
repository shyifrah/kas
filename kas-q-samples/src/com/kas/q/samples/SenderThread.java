package com.kas.q.samples;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class SenderThread extends AThread
{
  SenderThread(String name, int numOfMessages, int delay, Session session, Queue queue)
  {
    super(name, numOfMessages, delay, session, queue);
  }
  
  public void work()
  {
    try
    {
      MessageProducer producer = mSession.createProducer(mQueue);
      
      for (int i = 0; i < mNumOfMessages; i++)
      {
        String text = "shyifrah-" + Integer.toString(i);
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
