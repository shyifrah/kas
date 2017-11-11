package com.kas.q.impl.clients;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSender;
import com.kas.q.KasqMessageConsumer;
import com.kas.q.KasqMessageProducer;
import com.kas.q.KasqQueueSender;
import com.kas.q.KasqQueueSession;
import com.kas.q.KasqSession;

public class KasqClientsFactory
{
  public static MessageProducer createProducer(KasqSession session, Destination destination) throws JMSException
  {
    return new KasqMessageProducer(session, destination);
  }
  
  public static MessageConsumer createConsumer(KasqSession session, Destination destination) throws JMSException
  {
    return new KasqMessageConsumer(session, destination);
  }
  
  public static MessageConsumer createConsumer(KasqSession session, Destination destination, String messageSelector) throws JMSException
  {
    return new KasqMessageConsumer(session, destination, messageSelector);
  }
  
  public static MessageConsumer createConsumer(KasqSession session, Destination destination, String messageSelector, boolean noLocal) throws JMSException
  {
    return new KasqMessageConsumer(session, destination, messageSelector, noLocal);
  }
  
  public static QueueSender createQueueSender(KasqQueueSession session, Queue queue) throws JMSException
  {
    return new KasqQueueSender(session, queue);
  }
}
