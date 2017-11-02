package com.kas.q.impl;

import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import com.kas.infra.base.KasObject;

public class KasqSession extends KasObject implements Session
{
  KasqConnection mConnection;
  boolean    mTransacted;
  int        mAcknowledgeMode;
  int        mSessionMode;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqSession} object associated with the specified {@code Connection}
   * 
   * @param connection the associated {@code Connection}
   */
  KasqSession(KasqConnection connection)
  {
    this(connection, false, 0, 0);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqSession} object associated with the specified {@code Connection}
   * 
   * Parameters transacted and acknowledgeMode are not supported.
   * 
   * @param connection the associated {@code Connection}
   * @param transacted indicates whether the session will use a local transaction
   * @param acknowledgeMode when transacted is false, indicates how messages received by the session will be acknowledged
   */
  KasqSession(KasqConnection connection, boolean transacted, int acknowledgeMode)
  {
    this(connection, transacted, acknowledgeMode, 0);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqSession} object associated with the specified {@code Connection}
   * 
   * Parameter sessionMode is not supported.
   * 
   * @param connection the associated {@code Connection}
   * @param sessionMode the session mode that will be used
   */
  KasqSession(KasqConnection connection, int sessionMode)
  {
    this(connection, false, 0, sessionMode);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqSession} object associated with the specified {@code Connection}
   * 
   * Parameters transacted, acknowledgeMode and sessionMode are not supported.
   * 
   * @param connection the associated {@code Connection}
   * @param sessionMode the session mode that will be used
   */
  private KasqSession(KasqConnection connection, boolean transacted, int acknowledgeMode, int sessionMode)
  {
    mConnection      = connection;
    mTransacted      = transacted;
    mAcknowledgeMode = acknowledgeMode;
    mSessionMode     = sessionMode;
  }
  
  public Message createMessage() throws JMSException
  {
    return null;
  }

  public BytesMessage createBytesMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createBytesMessage()");
  }

  public MapMessage createMapMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createMapMessage()");
  }

  public ObjectMessage createObjectMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createObjectMessage()");
  }

  public ObjectMessage createObjectMessage(Serializable object) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createObjectMessage(Serializable)");
  }

  public StreamMessage createStreamMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createStreamMessage()");
  }

  public TextMessage createTextMessage() throws JMSException
  {
    return new KasqTextMessage();
  }

  public TextMessage createTextMessage(String text) throws JMSException
  {
    return new KasqTextMessage(text);
  }

  public boolean getTransacted() throws JMSException
  {
    return mTransacted;
  }

  public int getAcknowledgeMode() throws JMSException
  {
    return mAcknowledgeMode;
  }

  public void commit() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.commit()");
  }

  public void rollback() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.rollback()");
  }

  public void close() throws JMSException
  {
    //
    //
    //
  }

  public void recover() throws JMSException
  {
    //
    //
    //
  }

  public MessageListener getMessageListener() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.getMessageListener()");
  }

  public void setMessageListener(MessageListener listener) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.setMessageListener(MessageListener)");
  }

  public void run()
  {
    //
    //
    //
  }

  public MessageProducer createProducer(Destination destination) throws JMSException
  {
    return new KasqMessageProducer(this, destination);
  }

  public MessageConsumer createConsumer(Destination destination) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createConsumer(Destination)");
  }

  public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createConsumer(Destination, String)");
  }

  public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createConsumer(Destination, String, boolean)");
  }

  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedConsumer(Topic, String)");
  }

  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedConsumer(Topic, String, String)");
  }

  public Queue createQueue(String queueName) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createQueue(String)");
  }

  public Topic createTopic(String topicName) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createTopic(String)");
  }

  public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableSubscriber(Topic, String)");
  }

  public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableSubscriber(Topic, String, String, boolean)");
  }

  public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableConsumer(Topic, String)");
  }

  public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableConsumer(Topic, String, String, boolean)");
  }

  public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedDurableConsumer(Topic, String)");
  }

  public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedDurableConsumer(Topic, String, String)");
  }

  public QueueBrowser createBrowser(Queue queue) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createBrowser(Queue)");
  }

  public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createBrowser(Queue, String)");
  }

  public TemporaryQueue createTemporaryQueue() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createTemporaryQueue()");
  }

  public TemporaryTopic createTemporaryTopic() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createTemporaryTopic()");
  }

  public void unsubscribe(String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.unsubscribe(String)");
  }
  
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Transacted=").append(mTransacted).append("\n")
      .append(pad).append("  AcknowledgeMode=").append(mAcknowledgeMode).append("\n")
      .append(pad).append("  SessionMode=").append(mSessionMode).append("\n")
      .append(pad).append("  Connection=(").append(mConnection.toPrintableString(level + 2)).append(")\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
