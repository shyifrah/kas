package com.kas.q.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

public class KasqQueueSession extends KasqSession implements QueueSession
{
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueSession} object associated with the specified {@code Connection}
   * 
   * @param connection the associated {@code Connection}
   */
  public KasqQueueSession(KasqConnection connection)
  {
    super(connection);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueSession} object associated with the specified {@code Connection}
   * 
   * @param connection the associated {@code Connection}
   * @param transacted indicates whether the session will use a local transaction
   * @param acknowledgeMode when transacted is false, indicates how messages received by the session will be acknowledged
   */
  public KasqQueueSession(KasqConnection connection, boolean transacted, int acknowledgeMode)
  {
    super(connection, transacted, acknowledgeMode);
  }

  public Message createMessage() throws JMSException
  {
    return new KasqMessage();
  }

  public TextMessage createTextMessage() throws JMSException
  {
    return new KasqTextMessage();
  }

  public TextMessage createTextMessage(String text) throws JMSException
  {
    return new KasqTextMessage(text);
  }

  public QueueReceiver createReceiver(Queue queue) throws JMSException
  {
    throw new JMSException("Unsupported method: QueueSession.createReceiver(Queue)");
  }

  public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: QueueSession.createReceiver(Queue, String)");
  }

  public QueueSender createSender(Queue queue) throws JMSException
  {
    throw new JMSException("Unsupported method: QueueSession.createSender(Queue)");
  }
  
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
