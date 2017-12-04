package com.kas.q;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;

public class KasqSession extends AKasObject implements Session
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(KasqSession.class);
  
  /***************************************************************************************************************
   * 
   */
  KasqConnection mConnection;
  boolean        mTransacted;
  int            mAcknowledgeMode;
  int            mSessionMode;
  String         mSessionId;
  
  List<KasqMessageProducer> mProducers;
  List<KasqMessageConsumer> mConsumers;
  
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
    
    mProducers = new ArrayList<KasqMessageProducer>();
    mConsumers = new ArrayList<KasqMessageConsumer>();
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
    return new KasqBytesMessage();
  }

  /***************************************************************************************************************
   * 
   */
  public MapMessage createMapMessage() throws JMSException
  {
    return new KasqMapMessage();
  }

  /***************************************************************************************************************
   * 
   */
  public ObjectMessage createObjectMessage() throws JMSException
  {
    return new KasqObjectMessage();
  }

  /***************************************************************************************************************
   * 
   */
  public ObjectMessage createObjectMessage(Serializable object) throws JMSException
  {
    return new KasqObjectMessage(object);
  }
  
  /***************************************************************************************************************
   * 
   */
  public StreamMessage createStreamMessage() throws JMSException
  {
    return new KasqStreamMessage();
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
    // TODO: implement
    throw new JMSException("Unsupported method: Session.commit()");
  }

  /***************************************************************************************************************
   * 
   */
  public void rollback() throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.rollback()");
  }

  /***************************************************************************************************************
   * 
   */
  public void close() throws JMSException
  {
    // close all producers
    synchronized (mProducers)
    {
      for (KasqMessageProducer producer : mProducers)
        producer.close();
    }
    
    // close all consumers
    // when closing a consumer, we should wait for all consumers to finish their receive() calls
    synchronized (mConsumers)
    {
      for (KasqMessageConsumer consumer : mConsumers)
        consumer.close();
    }
  }

  /***************************************************************************************************************
   * 
   */
  public void recover() throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.recover()");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageListener getMessageListener() throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.getMessageListener()");
  }

  /***************************************************************************************************************
   * 
   */
  public void setMessageListener(MessageListener listener) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.setMessageListener(MessageListener)");
  }

  /***************************************************************************************************************
   * 
   */
  public void run()
  {
    // TODO: Unsupported method: Session.run()
  }

  /***************************************************************************************************************
   * 
   */
  public MessageProducer createProducer(Destination destination) throws JMSException
  {
    KasqMessageProducer producer = new KasqMessageProducer(this, destination);
    synchronized(mProducers)
    {
      mProducers.add(producer);
    }
    return producer;
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createConsumer(Destination destination) throws JMSException
  {
    KasqMessageConsumer consumer = new KasqMessageConsumer(this, destination);
    synchronized(mConsumers)
    {
      mConsumers.add(consumer);
    }
    return consumer;
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException
  {
    KasqMessageConsumer consumer = new KasqMessageConsumer(this, destination, messageSelector);
    synchronized(mConsumers)
    {
      mConsumers.add(consumer);
    }
    return consumer;
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException
  {
    KasqMessageConsumer consumer = new KasqMessageConsumer(this, destination, messageSelector, noLocal);
    synchronized(mConsumers)
    {
      mConsumers.add(consumer);
    }
    return consumer;
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createSharedConsumer(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createSharedConsumer(Topic, String, String)");
  }
  
  /***************************************************************************************************************
   * Locate a queue in the KAS/Q server.<br>
   * This method is used by {@code KasqClient} users.
   * 
   * @param queueName the name of the queue to be located
   * 
   * @return the located {@code KasqQueue} object, or null if none was located
   * 
   * @throws JMSException
   */
  public Queue locateQueue(String queueName) throws JMSException
  {
    return (KasqQueue)internalLocateDestination(queueName, EDestinationType.cQueue);
  }

  /***************************************************************************************************************
   * Locate a topic in the KAS/Q server.<br>
   * This method is used by {@code KasqClient} users.
   * 
   * @param topicName the name of the topic to be located
   * 
   * @return the located {@code KasqTopic} object, or null if none was located
   * 
   * @throws JMSException
   */
  public Topic locateTopic(String topicName) throws JMSException
  {
    return (KasqTopic)internalLocateDestination(topicName, EDestinationType.cTopic);
  }

  /***************************************************************************************************************
   * 
   */
  public Queue createQueue(String queueName) throws JMSException
  {
    return (KasqQueue)internalCreateDestination(queueName, EDestinationType.cQueue);
  }

  /***************************************************************************************************************
   * 
   */
  public Topic createTopic(String topicName) throws JMSException
  {
    return (KasqTopic)internalCreateDestination(topicName, EDestinationType.cTopic);
  }

  /***************************************************************************************************************
   * 
   */
  public TemporaryQueue createTemporaryQueue() throws JMSException
  {
    UniqueId uniqueId = UniqueId.generate();
    String queueName = "KAS.TEMP.Q" + uniqueId.toString();
    return (KasqQueue)internalCreateTemporaryDestination(queueName, EDestinationType.cQueue);
  }

  /***************************************************************************************************************
   * 
   */
  public TemporaryTopic createTemporaryTopic() throws JMSException
  {
    UniqueId uniqueId = UniqueId.generate();
    String topicName = "KAS.TEMP.T" + uniqueId.toString();
    return (KasqTopic)internalCreateTemporaryDestination(topicName, EDestinationType.cTopic);
  }

  /***************************************************************************************************************
   * 
   */
  public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createDurableSubscriber(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createDurableSubscriber(Topic, String, String, boolean)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createDurableConsumer(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createDurableConsumer(Topic, String, String, boolean)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createSharedDurableConsumer(Topic, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createSharedDurableConsumer(Topic, String, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public QueueBrowser createBrowser(Queue queue) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createBrowser(Queue)");
  }

  /***************************************************************************************************************
   * 
   */
  public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.createBrowser(Queue, String)");
  }

  /***************************************************************************************************************
   * 
   */
  public void unsubscribe(String name) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Session.unsubscribe(String)");
  }
  
  /***************************************************************************************************************
   * Send a Define message to the KAS/Q server to define a new destination and return the defined destination
   * 
   * @param name the destination name
   * @param type an integer representing the destination type: 1 - queue, 2 - topic
   * 
   * @throws JMSException 
   */
  private IKasqDestination internalCreateDestination(String name, EDestinationType type) throws JMSException
  {
    sLogger.debug("KasqSession::internalCreateDestination() - IN");
    
    if (name == null)
      throw new JMSException("Failed to create destination: Invalid destination name: [" + StringUtils.asString(name) + "]");
    
    IKasqDestination dest;
    int responseCode = IKasqConstants.cPropertyResponseCode_Fail;
    String msg = null;
    
    if (type == EDestinationType.cQueue)
      dest = new KasqQueue(name, "");
    else
      dest = new KasqTopic(name, "");
    
    try
    {
      KasqMessage defineRequest = new KasqMessage();
      defineRequest.setJMSMessageID("ID:" + UniqueId.generate().toString());
      defineRequest.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Define);
      defineRequest.setJMSDestination(dest);
      
      sLogger.debug("KasqSession::internalCreateDestination() - Sending define request via message: " + defineRequest.toPrintableString(0));
      IKasqMessage defineResponse = mConnection.internalSendAndReceive(defineRequest);
      
      sLogger.debug("KasqSession::internalCreateDestination() - Got response: " + defineResponse.toPrintableString(0));
      responseCode = defineResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
      if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
        dest = (IKasqDestination)defineResponse.getJMSDestination();
      else
        msg  = defineResponse.getStringProperty(IKasqConstants.cPropertyResponseMessage);
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqSession::internalCreateDestination() - Exception caught: ", e);
    }
    
    if (responseCode == IKasqConstants.cPropertyResponseCode_Fail)
      throw new JMSException("Destination creation failed: " + msg);
    
    sLogger.debug("KasqSession::internalCreateDestination() - OUT, Result=" + StringUtils.asString(dest));
    return dest;
  }
  
  /***************************************************************************************************************
   * Create of a temporary destination.
   * Since the scope of a temporary destination is the Connection object, the Connection object must be
   * aware of all created temporary destinations.
   * 
   * @param name the destination name
   * @param type the destination type
   * 
   * @throws JMSException 
   */
  private IKasqDestination internalCreateTemporaryDestination(String name, EDestinationType type) throws JMSException
  {
    return mConnection.internalCreateTemporaryDestination(name, type);
  }
  
  /***************************************************************************************************************
   * Delete a temporary destination.
   * Since the scope of a temporary destination is the Connection object, the Connection object must be
   * aware of all deleted temporary destinations.
   * 
   * @param name the destination name
   * @param type the destination type
   * 
   * @throws JMSException 
   */
  protected void internalDeleteTemporaryDestination(String name, EDestinationType type) throws JMSException
  {
    mConnection.internalDeleteTemporaryDestination(name, type);
  }
  
  /***************************************************************************************************************
   * Send a Locate message to the KAS/Q server to locate an existing destination and return it
   * 
   * @param name the destination name
   * @param type an integer representing the destination type: 1 - queue, 2 - topic
   * 
   * @throws JMSException 
   */
  private IKasqDestination internalLocateDestination(String name, EDestinationType type) throws JMSException
  {
    sLogger.debug("KasqSession::internalLocateDestination() - IN");
    
    if (name == null)
      throw new JMSException("Failed to locate destination: Invalid destination name: [" + StringUtils.asString(name) + "]");
    
    IKasqDestination dest = null;
    int responseCode = IKasqConstants.cPropertyResponseCode_Fail;
    
    try
    {
      KasqMessage locateRequest = new KasqMessage();
      locateRequest.setJMSMessageID("ID:" + UniqueId.generate().toString());
      locateRequest.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Locate);
      locateRequest.setStringProperty(IKasqConstants.cPropertyDestinationName, name);
      locateRequest.setIntProperty(IKasqConstants.cPropertyDestinationType, type.ordinal());
      
      sLogger.debug("KasqSession::internalLocateDestination() - Sending locate request via message: " + locateRequest.toPrintableString(0));
      IKasqMessage locateResponse = mConnection.internalSendAndReceive(locateRequest);
      
      sLogger.debug("KasqSession::internalLocateDestination() - Got response: " + locateResponse.toPrintableString(0));
      responseCode = locateResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
      if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
        dest = (IKasqDestination)locateResponse.getJMSDestination();
      
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqSession::internalCreateDestination() - Exception caught: ", e);
    }
    
    sLogger.debug("KasqSession::internalLocateDestination() - OUT, Result=" + StringUtils.asString(dest));
    return dest;
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
