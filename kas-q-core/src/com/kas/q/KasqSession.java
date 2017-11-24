package com.kas.q;

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
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
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
    return (KasqQueue)internalCreateDestination(queueName, IKasqConstants.cPropertyDestinationType_Queue);
  }

  /***************************************************************************************************************
   * 
   */
  public Topic createTopic(String topicName) throws JMSException
  {
    return (KasqTopic)internalCreateDestination(topicName, IKasqConstants.cPropertyDestinationType_Topic);
  }

  /***************************************************************************************************************
   * 
   */
  public TemporaryQueue createTemporaryQueue() throws JMSException
  {
    UniqueId uniqueId = UniqueId.generate();
    String queueName = "KAS.TEMP.Q" + uniqueId.toString();
    return (KasqQueue)internalCreateDestination(queueName, IKasqConstants.cPropertyDestinationType_Queue);
  }

  /***************************************************************************************************************
   * 
   */
  public TemporaryTopic createTemporaryTopic() throws JMSException
  {
    UniqueId uniqueId = UniqueId.generate();
    String topicName = "KAS.TEMP.T" + uniqueId.toString();
    return (KasqTopic)internalCreateDestination(topicName, IKasqConstants.cPropertyDestinationType_Topic);
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
   * Send a Define message to the KAS/Q server to define a new destination and return the defined destination
   * 
   * @param name the destination name
   * @param type an integer representing the destination type: 1 - queue, 2 - topic
   * 
   * @throws JMSException 
   */
  private IKasqDestination internalCreateDestination(String name, int type) throws JMSException
  {
    sLogger.debug("KasqSession::internalCreateDestination() - IN");
    
    if (name == null)
      throw new JMSException("Failed to create destination", "Invalid destination name: [" + StringUtils.asString(name) + "]");
    
    if ((type != IKasqConstants.cPropertyDestinationType_Queue) && (type != IKasqConstants.cPropertyDestinationType_Topic))
      throw new JMSException("Failed to create queue", "Invalid destination type: [" + type + "]");
    
    IKasqDestination dest;
    if (type == IKasqConstants.cPropertyDestinationType_Queue)
      dest = new KasqQueue(name, "");
    else
      dest = new KasqTopic(name, "");
    
    try
    {
      KasqMessage defineRequest = new KasqMessage();
      defineRequest.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Define);
      defineRequest.setJMSDestination(dest);
      
      sLogger.debug("KasqSession::internalCreateDestination() - Sending define request via message: " + defineRequest.toPrintableString(0));
      IPacket response = mConnection.internalSendAndReceive(defineRequest);
      if (response.getPacketClassId() == PacketHeader.cClassIdKasq)
      {
        IKasqMessage defineResponse = (IKasqMessage)response;
        sLogger.debug("KasqSession::internalCreateDestination() - Got this response: " + defineResponse.toPrintableString(0));
        int responseCode = defineResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
        if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
        {
          dest = (IKasqDestination)defineResponse.getJMSDestination();
        }
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqSession::internalCreateDestination() - Exception caught: ", e);
    }
    
    sLogger.debug("KasqSession::internalCreateDestination() - OUT, Result=" + StringUtils.asString(dest));
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
      .append(pad).append(")");
    return sb.toString();
  }
}
