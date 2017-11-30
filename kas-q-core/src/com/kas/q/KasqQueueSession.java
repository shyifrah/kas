package com.kas.q;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import com.kas.infra.utils.StringUtils;

public class KasqQueueSession extends KasqSession implements QueueSession
{
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueSession} object associated with the specified {@code KasqQueueConnection}
   * 
   * @param connection the associated {@code Connection}
   */
  public KasqQueueSession(KasqQueueConnection connection)
  {
    super(connection);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueSession} object associated with the specified {@code KasqQueueConnection}
   * 
   * @param connection the associated {@code Connection}
   * @param transacted indicates whether the session will use a local transaction
   * @param acknowledgeMode when transacted is false, indicates how messages received by the session will be acknowledged
   */
  public KasqQueueSession(KasqQueueConnection connection, boolean transacted, int acknowledgeMode)
  {
    super(connection, transacted, acknowledgeMode);
  }

  /***************************************************************************************************************
   * 
   */
  public QueueReceiver createReceiver(Queue queue) throws JMSException
  {
    KasqQueueReceiver consumer = new KasqQueueReceiver(this, queue);
    synchronized(mConsumers)
    {
      mConsumers.add(consumer);
    }
    return consumer;
  }

  /***************************************************************************************************************
   * 
   */
  public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException
  {
    KasqQueueReceiver consumer = new KasqQueueReceiver(this, queue, messageSelector);
    synchronized(mConsumers)
    {
      mConsumers.add(consumer);
    }
    return consumer;
  }

  /***************************************************************************************************************
   * 
   */
  public QueueSender createSender(Queue queue) throws JMSException
  {
    KasqQueueSender producer = new KasqQueueSender(this, queue);
    synchronized(mProducers)
    {
      mProducers.add(producer);
    }
    return producer;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  SessionId=").append(mSessionId).append("\n")
      .append(pad).append("  Transacted=").append(mTransacted).append("\n")
      .append(pad).append("  AcknowledgeMode=").append(mAcknowledgeMode).append("\n")
      .append(pad).append("  SessionMode=").append(mSessionMode).append("\n")
      .append(pad).append("  Connection=(").append(mConnection.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  MessageProducers=(")
      .append(StringUtils.asPrintableString(mProducers, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  MessageConsumers=(")
      .append(StringUtils.asPrintableString(mConsumers, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
