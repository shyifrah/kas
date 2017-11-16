package com.kas.q;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import com.kas.comm.IPacket;
import com.kas.comm.IMessenger;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.logging.ILogger;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.ConnectionReceiverTask;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.KasqMessageFactory;

public class KasqConnection extends AKasObject implements Connection
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
  protected ConnectionReceiverTask mReceiver;
  
  private Map<UniqueId, KasqSession> mOpenedSessions;
  
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
  KasqConnection(String host, int port, String userName, String password) throws JMSException
  {
    try
    {
      mOpenedSessions = new ConcurrentHashMap<UniqueId, KasqSession>();
      
      mMessenger = MessengerFactory.create(host, port, new KasqMessageFactory());
      
      mClientId = "CLNT" + UniqueId.generate().toString();
    }
    catch (Throwable e)
    {
      throw new JMSException("Connection creation failed", e.getMessage());
    }
    
    mReceiver = new ConnectionReceiverTask(mMessenger, this);
    
    boolean authenticated = authenticate(userName, password);
    if (!authenticated)
      throw new JMSException("Authentication failed");
  }
  
  /***************************************************************************************************************
   *  
   */
  public void start()
  {
    ThreadPool.execute(mReceiver);
    mStarted = true;
  }

  /***************************************************************************************************************
   *  
   */
  public void stop()
  {
    mStarted = false;
    ThreadPool.removeTask(mReceiver);
  }
  
  /***************************************************************************************************************
   * Returns the connection status.
   * 
   * @return true if the connection is started, false otherwise
   */
  public boolean isStarted()
  {
    return mStarted;
  }
  
  /***************************************************************************************************************
   *  
   */
  public Session createSession() throws JMSException
  {
    KasqSession session = new KasqSession(this);
    mOpenedSessions.put(session.getSessionId(), session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    KasqSession session = new KasqSession(this, transacted, acknowledgeMode);
    mOpenedSessions.put(session.getSessionId(), session);
    return session;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(int sessionMode) throws JMSException
  {
    KasqSession session = new KasqSession(this, sessionMode);
    mOpenedSessions.put(session.getSessionId(), session);
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
    try
    {
      stop();
      mMessenger.cleanup();
    }
    catch (Throwable e) {}
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
   * Get a KasqSession object from the OpenedSessions map
   * 
   * @param id the {@code UniqueId} of the session
   * 
   * @return the KasqSession with the specified id, or null if not found 
   */
  public KasqSession getOpenSession(UniqueId id)
  {
    return mOpenedSessions.get(id);
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
      KasqMessage authRequest = new KasqMessage();
      authRequest.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Authenticate);
      
      authRequest.setStringProperty(IKasqConstants.cPropertyUserName, userName);
      authRequest.setStringProperty(IKasqConstants.cPropertyPassword, password);
      if (userName.equals("admin"))
        authRequest.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, true);
      
      IPacket response = mMessenger.sendAndReceive(authRequest);
      
      if (response.getPacketClassId() == PacketHeader.cClassIdKasq)
      {
        IKasqMessage authResponse = (IKasqMessage)response;
        int responseCode = authResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
        if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
        {
          result = true;
        }
      }
    }
    catch (Throwable e) {}
    return result;
  }
  
  /***************************************************************************************************************
   * Send a message to the KAS/Q server by calling the messenger's send() message.
   * 
   * @param message the message to be sent
   * 
   * @throws JMSException if an I/O exception occurs 
   */
  synchronized void internalSend(IKasqMessage message) throws JMSException
  {
    try
    {
      mMessenger.send(message);
    }
    catch (Throwable e)
    {
      throw new JMSException("Failed to send message", e.getMessage());
    }
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
      .append(pad).append(")");
    return sb.toString();
  }
}
