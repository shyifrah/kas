package com.kas.q.impl.conn;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import com.kas.containers.CappedContainerProxy;
import com.kas.containers.CappedContainersFactory;
import com.kas.containers.CappedHashMap;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.ThreadPool;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.MessageType;
import com.kas.q.ext.impl.MessageSerializer;
import com.kas.q.ext.impl.Messenger;
import com.kas.q.impl.messages.KasqTextMessage;

public class KasqConnection extends KasObject implements Connection
{
  /***************************************************************************************************************
   *  
   */
  static class ConnectionReader implements Runnable
  {
    Messenger mMessenger = null;
    
    ConnectionReader(Messenger messenger)
    {
      mMessenger = messenger;
    }
    
    public void run()
    {
      try
      {
        IMessage message = MessageSerializer.deserialize(mMessenger.getInputStream());
        while (message != null)
        {
          deliver(message);
          message = MessageSerializer.deserialize(mMessenger.getInputStream());
        }
      }
      catch (Throwable e) {}
    }
    
    public void deliver(IMessage message)
    {
      //
      // TODO: need to find out which session has the message consumer
      //       that listens to the Destination (getJMSDestination())
      //       and put the message in the awaiting messages queue for that session.
      //
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  private static final String cDefaultUserName = "kas";
  private static final String cDefaultPassword = "kas";
  
  /***************************************************************************************************************
   *  
   */
  protected ILogger   mLogger;
  protected Messenger mOutgoingMessenger = null;
  protected Messenger mIncomingMessenger = null;
  protected boolean   mStarted   = false;
  protected String    mClientId  = null;
  
  protected Runnable  mReader = null;
  
  private CappedContainerProxy mSessionsMapProxy;
  private CappedHashMap<String, KasqSession> mSessionsMap;
  
  /***************************************************************************************************************
   * Constructs a Connection object to the specified host/port combination, using the default user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * 
   * @throws JMSException 
   */
  KasqConnection(String host, int port) throws JMSException
  {
    this(host, port, cDefaultUserName, cDefaultPassword);
  }
  
  /***************************************************************************************************************
   * Constructs a Connection object to the specified host/port combination, using the specified user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * @param userName the caller's user name
   * @param password the caller's password
   * 
   * @throws JMSException  
   */
  @SuppressWarnings("unchecked")
  KasqConnection(String host, int port, String userName, String password) throws JMSException
  {
    try
    {
      mOutgoingMessenger = Messenger.Factory.create(host, port);
      mIncomingMessenger = Messenger.Factory.create(host, port);
      
      mReader = new ConnectionReader(mIncomingMessenger);
      
      mSessionsMapProxy = new CappedContainerProxy("messaging.sessions.map", mLogger);
      mSessionsMap = (CappedHashMap<String, KasqSession>)CappedContainersFactory.createMap(mSessionsMapProxy);
      
      mClientId = "CLNT" + new UniqueId().toString();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("Connection creation failed", "Exception caught. ", e);
    }
    
    boolean authenticated = authenticate(userName, password);
    if (!authenticated)
      throw new JMSException("Authentication failed");
  }
  
  /***************************************************************************************************************
   *  
   */
  public void start()
  {
    mStarted = true;
    ThreadPool.submit(mReader);
  }

  /***************************************************************************************************************
   *  
   */
  public void stop()
  {
    mStarted = false;
    ThreadPool.remove(mReader);
  }
  
  /***************************************************************************************************************
   *  
   */
  public Session createSession() throws JMSException
  {
    KasqSession session = new KasqSession(this);
    putSession(session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    KasqSession session = new KasqSession(this, transacted, acknowledgeMode);
    putSession(session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(int sessionMode) throws JMSException
  {
    KasqSession session = new KasqSession(this, sessionMode);
    putSession(session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public String getClientID() throws JMSException
  {
    return mClientId;
  }

  /***************************************************************************************************************
   *  
   */
  public void setClientID(String clientID) throws JMSException
  {
    mClientId = clientID;
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionMetaData getMetaData() throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.getMetaData()");
  }

  /***************************************************************************************************************
   *  
   */
  public ExceptionListener getExceptionListener() throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.getExceptionListener()");
  }

  /***************************************************************************************************************
   *  
   */
  public void setExceptionListener(ExceptionListener listener) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.setExceptionListener(ExceptionListener)");
  }

  /***************************************************************************************************************
   *  
   */
  public void close() throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.close()");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createConnectionConsumer(destination, String, ServerSessionPool, int)");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createSharedConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createDurableConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createSharedDurableConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }
  
  /***************************************************************************************************************
   * Authenticate the caller
   * 
   * @param userName the user name of the caller
   * @param password the password of the caller
   * 
   * @return true if authentication succeeded, false otherwise
   * 
   * @throws JMSException 
   */
  private boolean authenticate(String userName, String password) throws JMSException
  {
    boolean result = false;
    StringBuffer requestBody = new StringBuffer();
    requestBody.append("Cmd:Auth,Client:")
      .append(mClientId)
      .append(",User:")
      .append(userName)
      .append(",Pass:")
      .append(password);
    
    IMessage response = null;
    try
    {
      KasqTextMessage request = new KasqTextMessage(requestBody.toString());
      MessageSerializer.serialize(mOutgoingMessenger.getOutputStream(), request);
      response = MessageSerializer.deserialize(mIncomingMessenger.getInputStream());
    }
    catch (Throwable e) {}
    
    if ((response != null) && (response.getMessageType() == MessageType.cTextMessage))
    {
      String responseBody = ((KasqTextMessage)response).getText();
      if (responseBody.indexOf("Resp:Okay") > 0)
      {
        result = true;
      }
    }
    return result;
  }
  
  /***************************************************************************************************************
   * Put a session in the SessionsMap.
   * 
   * @param session session to put
   */
  private synchronized void putSession(KasqSession session)
  {
    mSessionsMap.put(session.mSessionId, session);
  }
  
  /***************************************************************************************************************
   * Get a session from the SessionsMap.
   * 
   * @param id session-id to get
   * 
   * @return the session associated with specified id, or null if not found
   */
  private synchronized KasqSession getSession(String id)
  {
    return mSessionsMap.get(id);
  }
  
  /***************************************************************************************************************
   * Delete a session from the SessionsMap.
   * 
   * @param id session-id to delete
   * 
   * @return the session associated with specified id, or null if not found
   */
  private synchronized KasqSession delSession(String id)
  {
    return mSessionsMap.remove(id);
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Started=").append(mStarted).append("\n")
      .append(pad).append("  ClientId=").append(mClientId).append("\n")
      .append(pad).append("  OutgoingMessenger=(").append(mOutgoingMessenger.toPrintableString(level + 1)).append(")\n")
      .append(pad).append("  IncomingMessenger=(").append(mIncomingMessenger.toPrintableString(level + 1)).append(")\n")
      .append(pad).append("  Sessions=(").append(mSessionsMap.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
