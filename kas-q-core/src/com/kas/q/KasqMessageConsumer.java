package com.kas.q;

import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.GetRequest;

public class KasqMessageConsumer extends AKasObject implements MessageConsumer
{
  private static ILogger sLogger = LoggerFactory.getLogger(KasqMessageConsumer.class);
  
  protected KasqSession      mSession         = null;
  protected IKasqDestination mDestination     = null;
  protected String           mMessageSelector = null;
  protected MessageListener  mMessageListener = null;
  protected boolean          mNoLocal         = false;
  protected UniqueId         mConsumerId;
  protected KasqQueue        mConsumerQueue;
  protected Thread           mExecutingThread;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageConsumer} object for the specified {@code Destination}
   * 
   * @param session the session associated with this {@code MessageConsumer}
   * @param destination the destination from which messages will be consumed
   * 
   * @throws JMSException 
   */
  KasqMessageConsumer(KasqSession session, Destination destination) throws JMSException
  {
    this(session, destination, null);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageConsumer} object for the specified {@code Destination}, using
   * the specified message selector
   * 
   * @param session the session associated with this {@code MessageConsumer}
   * @param destination the destination from which messages will be consumed
   * @param messageSelector only messages with properties matching the message selector expression are delivered.
   *   A value of null or an empty string indicates that there is no message selector for the message consumer.
   * 
   * @throws JMSException 
   */
  KasqMessageConsumer(KasqSession session, Destination destination, String messageSelector) throws JMSException
  {
    this(session, destination, messageSelector, false);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMessageConsumer} object for the specified {@code Destination}, using
   * the specified message selector and the {@code noLocal} parameter
   * 
   * @param session the session associated with this {@code MessageConsumer}
   * @param destination the destination from which messages will be consumed
   * @param messageSelector only messages with properties matching the message selector expression are delivered.
   *   A value of null or an empty string indicates that there is no message selector for the message consumer.
   * @param noLocal if true, and the destination is a topic, then the MessageConsumer will not receive
   *   messages published to the topic by its own connection.
   *   
   * @throws JMSException 
   */
  KasqMessageConsumer(KasqSession session, Destination destination, String messageSelector, boolean noLocal) throws JMSException
  {
    mSession = session;
    
    if (destination == null)
      throw new InvalidDestinationException("Unsupported destination", "Null destination");
    
    if (!(destination instanceof IKasqDestination))
      throw new InvalidDestinationException("Unsupported destination", "Destination is not managed by KAS/Q");
    
    mDestination = (IKasqDestination)destination;
    
    mNoLocal = noLocal;
    mMessageSelector = messageSelector;
    if ((messageSelector != null) && (messageSelector.length() == 0))
      mMessageSelector = null;
    
    mConsumerId = UniqueId.generate();
    mConsumerQueue = (KasqQueue)mSession.createTemporaryQueue();
  }
  
  /***************************************************************************************************************
   *  
   */
  public void close() throws JMSException
  {
    if (mExecutingThread != null)
      mExecutingThread.interrupt();
    
    mSession.internalDeleteTemporaryDestination(mConsumerQueue.getName(), EDestinationType.cQueue);
  }

  /***************************************************************************************************************
   *  
   */
  public MessageListener getMessageListener() throws JMSException
  {
    return mMessageListener;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setMessageListener(MessageListener listener) throws JMSException
  {
    mMessageListener = listener;
  }

  /***************************************************************************************************************
   *  
   */
  public String getMessageSelector() throws JMSException
  {
    return mMessageSelector;
  }

  /***************************************************************************************************************
   *  
   */
  public Message receive() throws JMSException
  {
    return internalReceive(0L);
  }

  /***************************************************************************************************************
   *  
   */
  public Message receive(long timeout) throws JMSException
  {
    return internalReceive(timeout);
  }

  /***************************************************************************************************************
   *  
   */
  public Message receiveNoWait() throws JMSException
  {
    return internalReceive(-1L);
  }
  
  /***************************************************************************************************************
   * Consume a message from the designated destination.<br>
   * First we call {@link #internalPrepareConsumeRequest()} to prepare a request message. This message will be
   * sent to the KAS/Q server which will send a message matching the filtering criteria to the consumer queue.
   * The consume request will be sent timeout/1000 times, and each time the consumer will wait for the message
   * from the KAS/Q server 1 second.
   * 
   * @param timeout The number of milliseconds to wait for a consumed message. A value of -1L means no waiting
   * should take place. A value of 0L means wait forever.
   *        
   * @return the consumed message, or null if no message was returned
   */
  private Message internalReceive(long timeout) throws JMSException
  {
    sLogger.debug("KasqMessageConsumer::internalReceive() - IN");
    
    mExecutingThread = Thread.currentThread();
    
    IKasqMessage message = null;
    boolean infinite = timeout == 0;
    boolean closing = false;
    long repeat = timeout == -1L ? 1 : timeout / 1000; 
    
    for (long i = 0; ((i < repeat) || (infinite)) && (message == null) && (!closing); ++i)
    {
      GetRequest getRequest = new GetRequest(mDestination.getName(), mDestination.getType(), mNoLocal, mMessageSelector, mConsumerQueue.getName(), mSession.getSessionId());
      IKasqMessage requestMessage = getRequest.createRequestMessage();
      if (requestMessage == null)
        throw new JMSException("Failed to receive message. Could not create a consume request");
     
      sLogger.debug("Sending get request via message: " + requestMessage.toPrintableString(0));
      mSession.internalSend(requestMessage);
      try
      {
        message = mConsumerQueue.get(1000);
      }
      catch (InterruptedException e)
      {
        closing = true;
      }
      sLogger.debug("Got response: " + StringUtils.asPrintableString(message));
    }
    
    sLogger.debug("KasqMessageConsumer::internalReceive() - OUT, Result=" + StringUtils.asString(message));
    
    mExecutingThread = null;
    
    return message;
  }
  
  /***************************************************************************************************************
   * Get the consumer's identifier
   * 
   * @return The consumer's identifier 
   */
  public UniqueId getConsumerId()
  {
    return mConsumerId;
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
