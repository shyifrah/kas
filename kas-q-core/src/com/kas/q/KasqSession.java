package com.kas.q;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.IKasqMessage;

public class KasqSession extends AKasObject implements Session
{
  /***************************************************************************************************************
   * 
   */
  KasqConnection mConnection;
  boolean        mTransacted;
  int            mAcknowledgeMode;
  int            mSessionMode;
  String         mSessionId;
  private Map<String, KasqQueue> mOpenedQueues;
  private Map<String, KasqTopic> mOpenedTopics;
  
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
   * @param transacted indicates whether the session will use a local transaction
   * @param acknowledgeMode when transacted is false, indicates how messages received by the session will be acknowledged
   * @param sessionMode the session mode that will be used
   */
  KasqSession(KasqConnection connection, boolean transacted, int acknowledgeMode, int sessionMode)
  {
    mConnection      = connection;
    mTransacted      = transacted;
    mAcknowledgeMode = acknowledgeMode;
    mSessionMode     = sessionMode;
    mSessionId       = UniqueId.generate().toString();
    
    mOpenedQueues = new ConcurrentHashMap<String, KasqQueue>();
    mOpenedTopics = new ConcurrentHashMap<String, KasqTopic>();
  }
  
  /***************************************************************************************************************
   * 
   */
  public Message createMessage() throws JMSException
  {
    return new KasqMessage();
  }

  /***************************************************************************************************************
   * 
   */
  public BytesMessage createBytesMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createBytesMessage()");
  }

  /***************************************************************************************************************
   * 
   */
  public MapMessage createMapMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createMapMessage()");
  }

  /***************************************************************************************************************
   * 
   */
  public ObjectMessage createObjectMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createObjectMessage()");
  }

  /***************************************************************************************************************
   * 
   */
  public ObjectMessage createObjectMessage(Serializable object) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createObjectMessage(Serializable)");
  }
  
  /***************************************************************************************************************
   * 
   */
  public StreamMessage createStreamMessage() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createStreamMessage()");
  }

  /***************************************************************************************************************
   * 
   */
  public TextMessage createTextMessage() throws JMSException
  {
    return new KasqTextMessage();
  }

  /***************************************************************************************************************
   * 
   */
  public TextMessage createTextMessage(String text) throws JMSException
  {
    return new KasqTextMessage(text);
  }

  /***************************************************************************************************************
   * 
   */
  public boolean getTransacted() throws JMSException
  {
    return mTransacted;
  }

  /***************************************************************************************************************
   * 
   */
  public int getAcknowledgeMode() throws JMSException
  {
    return mAcknowledgeMode;
  }

  /***************************************************************************************************************
   * 
   */
  public void commit() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.commit()");
  }

  /***************************************************************************************************************
   * 
   */
  public void rollback() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.rollback()");
  }

  /***************************************************************************************************************
   * 
   */
  public void close() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.close()");
  }

  /***************************************************************************************************************
   * 
   */
  public void recover() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.recover()");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageListener getMessageListener() throws JMSException
  {
    throw new JMSException("Unsupported method: Session.getMessageListener()");
  }

  /***************************************************************************************************************
   * 
   */
  public void setMessageListener(MessageListener listener) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.setMessageListener(MessageListener)");
  }

  /***************************************************************************************************************
   * 
   */
  public void run()
  {
    // Unsupported method: Session.run()
  }

  /***************************************************************************************************************
   * 
   */
  public MessageProducer createProducer(Destination destination) throws JMSException
  {
    return new KasqMessageProducer(this, destination);
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createConsumer(Destination destination) throws JMSException
  {
    return new KasqMessageConsumer(this, destination);
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException
  {
    return new KasqMessageConsumer(this, destination, messageSelector);
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException
  {
    return new KasqMessageConsumer(this, destination, messageSelector, noLocal);
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedConsumer(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedConsumer(Topic, String, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public Queue createQueue(String queueName) throws JMSException
  {
    return internalCreateQueue(queueName);
  }

  /***************************************************************************************************************
   * 
   */
  public Topic createTopic(String topicName) throws JMSException
  {
    return internalCreateTopic(topicName);
  }

  /***************************************************************************************************************
   * 
   */
  public TemporaryQueue createTemporaryQueue() throws JMSException
  {
    UniqueId uniqueId = UniqueId.generate();
    String queueName = "KAS.TEMP.Q" + uniqueId.toString();
    return internalCreateQueue(queueName);
  }

  /***************************************************************************************************************
   * 
   */
  public TemporaryTopic createTemporaryTopic() throws JMSException
  {
    UniqueId uniqueId = UniqueId.generate();
    String topicName = "KAS.TEMP.T" + uniqueId.toString();
    return internalCreateTopic(topicName);
  }

  /***************************************************************************************************************
   * 
   */
  public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableSubscriber(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableSubscriber(Topic, String, String, boolean)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableConsumer(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createDurableConsumer(Topic, String, String, boolean)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedDurableConsumer(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createSharedDurableConsumer(Topic, String, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public QueueBrowser createBrowser(Queue queue) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createBrowser(Queue)");
  }

  /***************************************************************************************************************
   * 
   */
  public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.createBrowser(Queue, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public void unsubscribe(String name) throws JMSException
  {
    throw new JMSException("Unsupported method: Session.unsubscribe(String)");
  }
  
  /***************************************************************************************************************
   * Create a queue with the specified name and add it to the OpenedQueues map
   * 
   * @param name the name of the queue
   * 
   * @return a {@code KasqQueue} object
   * 
   * @throws JMSException if {@code name} is null
   */
  private KasqQueue internalCreateQueue(String name) throws JMSException
  {
    if (name == null)
      throw new JMSException("Failed to create queue", "Null name");
    
    KasqQueue q = new KasqQueue(name, "");
    synchronized (mOpenedQueues)
    {
      mOpenedQueues.put(name,  q);
    }
    return q;
  }
  
  /***************************************************************************************************************
   * Create a topic with the specified name and add it to the OpenedTopics map
   * 
   * @param name the name of the topic
   * 
   * @return a {@code KasqTopic} object
   * 
   * @throws JMSException if {@code name} is null
   */
  private KasqTopic internalCreateTopic(String name) throws JMSException
  {
    if (name == null)
      throw new JMSException("Failed to create topic", "Null name");
    
    KasqTopic t = new KasqTopic(name, "");
    synchronized (mOpenedTopics)
    {
      mOpenedTopics.put(name,  t);
    }
    return t;
  }
  
  /***************************************************************************************************************
   * Send a message to the KAS/Q server
   * 
   * @param message the message to be sent
   * 
   * @throws JMSException if an exception is thrown from the Connection object 
   */
  synchronized void internalSend(IKasqMessage message) throws JMSException
  {
    if (message != null)
      mConnection.internalSend(message);
  }
  
  /***************************************************************************************************************
   * Send a message to the KAS/Q server and get a reply
   * 
   * @param message the message to be sent
   * 
   * @return the reply
   * 
   * @throws JMSException if an exception is thrown from the Connection object
   */
  synchronized IKasqMessage internalSendAndReceive(IKasqMessage message) throws JMSException
  {
    IKasqMessage reply = null;
    if (message != null)
      reply = mConnection.internalSendAndReceive(message);
    
    return reply;
  }
  
  /***************************************************************************************************************
   * Get a KasqQueue object from the OpenedQueues map
   * 
   * @param name the name of the queue
   * 
   * @return the KasqQueue with the specified name, or null if not found 
   */
  public KasqQueue getOpenQueue(String name)
  {
    return mOpenedQueues.get(name);
  }
  
  /***************************************************************************************************************
   * Get a KasqTopic object from the OpenedTopics map
   * 
   * @param name the name of the topic
   * 
   * @return the KasqTopic with the specified name, or null if not found 
   */
  public KasqTopic getOpenTopic(String name)
  {
    return mOpenedTopics.get(name);
  }
  
  /***************************************************************************************************************
   * Get the session's identifier
   * 
   * @return The session's identifier
   */
  public String getSessionId()
  {
    return mSessionId;
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
      .append(pad).append("  Connection=(").append(mConnection.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
