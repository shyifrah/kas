package com.kas.q.impl.clients;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;
import com.kas.q.impl.KasqQueueSession;

public class KasqQueueSender extends KasqMessageProducer implements QueueSender
{
  /***************************************************************************************************************
   * 
   */
  private boolean     mDisableMessageId = false;
  private boolean     mDisableMessageTimestamp = false;
  private int         mDeliveryMode = DeliveryMode.PERSISTENT;
  private int         mPriority = 0;
  private long        mTimeToLive = 0;
  private long        mDeliveryDelay = 0;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageProducer} object with a default {@code Destination}
   * 
   * @param session the session associated with this {@code MessageProducer}
   * @param destination a default destination associated with the {@code MessageProducer}
   */
  KasqQueueSender(KasqQueueSession qsession, Queue queue)
  {
    super(qsession, queue);
  }
  
  /***************************************************************************************************************
   * 
   */
  public Queue getQueue()
  {
    return (Queue)mDestination;
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Queue queue, Message message) throws JMSException
  {
    super.send(queue, message, null);
  }

  /***************************************************************************************************************
   * 
   */
  public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    super.send(queue, message, deliveryMode, priority, timeToLive, null);
  }

  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Destination=").append(mDestination).append("\n")
      .append(pad).append("  MessageId disabled=").append(mDisableMessageId).append("\n")
      .append(pad).append("  Timestamp disabled=").append(mDisableMessageTimestamp).append("\n")
      .append(pad).append("  Default DeliveryMode=").append(mDeliveryMode).append("\n")
      .append(pad).append("  Default Priority=").append(mPriority).append("\n")
      .append(pad).append("  Default TTL=").append(mTimeToLive).append("\n")
      .append(pad).append("  Default DeliveryDelay=").append(mDeliveryDelay).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
