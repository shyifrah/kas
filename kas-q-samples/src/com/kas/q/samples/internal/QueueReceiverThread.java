package com.kas.q.samples.internal;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;

public class QueueReceiverThread extends AThread
{
  public static final String cProperty_ReceiveMode = "receive_mode";
  public static final String cProperty_ReceiveMode_NoWait = "immed";
  public static final String cProperty_ReceiveMode_TimedWait = "wait";
  public static final String cProperty_ReceiveMode_InfiniteWait = "infinite";
  public static final String cProperty_ReceiveTimeout = "receive_timeout";
  
  private MessageTrigger mTrigger = new MessageTrigger();
  
  public QueueReceiverThread(Properties threadParams) throws KasException
  {
    super(threadParams);
  }
  
  public void work()
  {
    try
    {
      MessageConsumer consumer;
      if (mQueueSession == null)
      {
        consumer = mQueueSession.createReceiver(mQueue);
      }
      else
      {
        consumer = mSession.createConsumer(mQueue);
      }
      consumer.setMessageListener(mTrigger);
      
      for (int i = 1; i <= mNumOfMessages; i++)
      {
        Message msg = receiveOneMessage(consumer);
        System.out.println("received message " + i + ": " + (msg == null ? "null" : msg.toString()));
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
    long receiveTimeout = mProperties.getLongProperty(cProperty_ReceiveTimeout, 3000);
    
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
