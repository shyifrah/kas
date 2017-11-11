package com.kas.q;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import com.kas.comm.IMessage;
import com.kas.comm.IMessenger;
import com.kas.comm.impl.MessageType;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.messages.AuthenticateRequestMessage;
import com.kas.comm.messages.ResponseMessage;
import com.kas.containers.CappedContainerProxy;
import com.kas.containers.CappedContainersFactory;
import com.kas.containers.CappedHashMap;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;

public class KasqConnection extends KasObject implements Connection
{
  /***************************************************************************************************************
   *  
   */
  private static final String cDefaultUserName = "kas";
  private static final String cDefaultPassword = "kas";
  
  /***************************************************************************************************************
   *  
   */
  protected ILogger mLogger;
  protected boolean mStarted  = false;
  protected String  mClientId = null;
  
  protected IMessenger mMessenger;

  private CappedContainerProxy mSessionsMapProxy;
  private CappedHashMap<String, KasqSession> mSessionsMap;
  
  /***************************************************************************************************************
   * Constructs a Connection object to the specified host/port combination, using the default user identity
   * 
   * @param host host name or IP-address of the KasQ server
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
      mSessionsMapProxy = new CappedContainerProxy("messaging.sessions.map", mLogger);
      mSessionsMap = (CappedHashMap<String, KasqSession>)CappedContainersFactory.createMap(mSessionsMapProxy);
      
      mMessenger = MessengerFactory.create(host, port);
      
      mClientId = "CLNT" + UniqueId.generate().toString();
    }
    catch (Throwable e)
    {
      throw new JMSException("Connection creation failed", e.getMessage());
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
  }

  /***************************************************************************************************************
   *  
   */
  public void stop()
  {
    mStarted = false;
  }
  
  /***************************************************************************************************************
   *  
   */
  public Session createSession() throws JMSException
  {
    KasqSession session = new KasqSession(this);
    addSession(session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    KasqSession session = new KasqSession(this, transacted, acknowledgeMode);
    addSession(session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(int sessionMode) throws JMSException
  {
    KasqSession session = new KasqSession(this, sessionMode);
    addSession(session);
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
    try
    {
      AuthenticateRequestMessage authRequest = new AuthenticateRequestMessage(userName, password);
      IMessage response = mMessenger.sendAndReceive(authRequest);
      
      if (response.getMessageType() == MessageType.cResponseMessage)
      {
        ResponseMessage authResponse = (ResponseMessage)response;
        result = authResponse.succeeded();
      }
    }
    catch (Throwable e) {}
    return result;
  }
  
  /***************************************************************************************************************
   * Add a session to the sessions map.
   * 
   * @param session session to add
   */
  private synchronized void addSession(KasqSession session)
  {
    mSessionsMap.put(session.mSessionId, session);
  }
  
  /***************************************************************************************************************
   * Get a session from the sessions map.
   * 
   * @param id session-id to retrieve
   * 
   * @return the session associated with the specified id, or {@code null} if not found
   */
  private synchronized KasqSession getSession(String id)
  {
    return mSessionsMap.get(id);
  }
  
  /***************************************************************************************************************
   * Delete a session from the sessions map.
   * 
   * @param id session-id to delete
   * 
   * @return the session associated with the specified id, or {@code null} if not found
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
      .append(pad).append("  Sessions=(").append(mSessionsMap.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
