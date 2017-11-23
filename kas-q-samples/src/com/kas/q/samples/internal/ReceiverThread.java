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
  public static final String cProperty_ReceiveMode = "receive_mode";
  public static final String cProperty_ReceiveMode_NoWait = "immed";
  public static final String cProperty_ReceiveMode_TimedWait = "wait";
  public static final String cProperty_ReceiveMode_InfiniteWait = "infinite";
  public static final String cProperty_ReceiveTimeout = "receive_timeout";
  
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
        Message msg = receiveOneMessage(consumer);
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
  
  private Message receiveOneMessage(MessageConsumer consumer) throws JMSException
  {
    String receiveMode  = mProperties.getStringProperty(cProperty_ReceiveMode, cProperty_ReceiveMode_InfiniteWait);
    long receiveTimeout = mProperties.getLongProperty(cProperty_ReceiveTimeout, 10000);
    
    // get message immediately
    if (cProperty_ReceiveMode_NoWait.equalsIgnoreCase(receiveMode))
    {
      return consumer.receiveNoWait();
    }
    else // wait with timeout
    if (cProperty_ReceiveMode_TimedWait.equalsIgnoreCase(receiveMode))
    {
      return consumer.receive(receiveTimeout);
    }
    else // infinite wait
    {
      return consumer.receive();
    }
  }
}
