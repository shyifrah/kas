package com.kas.q;

import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.IKasqDestination;

public class KasqMessageConsumer extends AKasObject implements MessageConsumer
{
  protected KasqSession      mSession         = null;
  protected IKasqDestination mDestination     = null;
  protected String           mMessageSelector = null;
  protected MessageListener  mMessageListener = null;
  protected boolean          mNoLocal         = false;
  
  private   UniqueId         mConsumerId; 
  
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
    
    if ((messageSelector == null) || (messageSelector.length() == 0))
      mMessageSelector = null;
    else
      mMessageSelector = messageSelector;
    
    mNoLocal = noLocal;
    
    mConsumerId = UniqueId.generate();
  }
  
  /***************************************************************************************************************
   *  
   */
  public void close() throws JMSException
  {
    throw new JMSException("Unsupported method: MessageConsumer.close()");
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
    throw new JMSException("Unsupported method: MessageConsumer.receive()");
  }

  /***************************************************************************************************************
   *  
   */
  public Message receive(long timeout) throws JMSException
  {
    throw new JMSException("Unsupported method: MessageConsumer.receive(long)");
  }

  /***************************************************************************************************************
   *  
   */
  public Message receiveNoWait() throws JMSException
  {
    throw new JMSException("Unsupported method: MessageConsumer.receiveNoWait()");
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