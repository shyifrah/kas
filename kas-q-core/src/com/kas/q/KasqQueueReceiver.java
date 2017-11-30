package com.kas.q;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;

public class KasqQueueReceiver extends KasqMessageConsumer implements QueueReceiver
{
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueReceiver} object for the specified {@code Queue}
   * 
   * @param session the session associated with this {@code QueueReceiver}
   * @param queue the queue from which messages will be consumed
   * 
   * @throws JMSException 
   */
  KasqQueueReceiver(KasqQueueSession session, Queue queue) throws JMSException
  {
    super(session, queue);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueReceiver} object for the specified {@code Queue}, using
   * the specified message selector
   * 
   * @param session the session associated with this {@code QueueReceiver}
   * @param queue the queue from which messages will be consumed
   * @param messageSelector only messages with properties matching the message selector expression are delivered.
   *   A value of null or an empty string indicates that there is no message selector for the message consumer.
   * 
   * @throws JMSException 
   */
  KasqQueueReceiver(KasqQueueSession session, Queue queue, String messageSelector) throws JMSException
  {
    super(session, queue, messageSelector, false);
  }
  
  /***************************************************************************************************************
   *  
   */
  public Queue getQueue() throws JMSException
  {
    return (KasqQueue)mDestination;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  ConsumerId=").append(mConsumerId).append("\n")
      .append(pad).append("  Destination=").append(mDestination).append("\n")
      .append(pad).append("  MessageSelector=").append(mMessageSelector).append("\n")
      .append(pad).append("  NoLocal=").append(mNoLocal).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }  
}
