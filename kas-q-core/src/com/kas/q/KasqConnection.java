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
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.ReceiverTask;
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
  private static ILogger sLogger = LoggerFactory.getLogger(KasqConnection.class);
  
  /***************************************************************************************************************
   *  
   */
  protected boolean mStarted  = false;
  protected String  mClientId = null;
  
  protected String mUserName = null;
  protected String mPassword = null;
  
  protected boolean mPriviliged = false;
  
  protected IMessenger mMessenger;
  protected ReceiverTask mReceiver;
  
  private Map<String, KasqSession> mOpenedSessions;
  
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
    sLogger.debug("KasqConnection::KasqConnection() - IN");
    try
    {
      mOpenedSessions = new ConcurrentHashMap<String, KasqSession>();
      mMessenger = MessengerFactory.create(host, port, new KasqMessageFactory());
      mClientId = "CLNT" + UniqueId.generate().toString();
      
      mUserName = userName;
      mPassword = password;
    }
    catch (Throwable e)
    {
      throw new JMSException("Connection creation failed", e.getMessage());
    }
    
    mReceiver = new ReceiverTask(mMessenger, this);
    
    boolean authenticated = authenticate(userName, password);
    if (!authenticated)
      throw new JMSException("Authentication failed");
    
    sLogger.debug("KasqConnection::KasqConnection() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public void start()
  {
    sLogger.debug("KasqConnection::start() - IN");
    mReceiver.start();
    mStarted = true;
    sLogger.debug("KasqConnection::start() - OUT");
  }

  /***************************************************************************************************************
   *  
   */
  public void stop()
  {
    sLogger.debug("KasqConnection::stop() - IN");
    mReceiver.interrupt();
    mStarted = false;
    sLogger.debug("KasqConnection::stop() - OUT");
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
   * @param id the ID of the session
   * 
   * @return the KasqSession with the specified id, or null if not found 
   */
  public KasqSession getOpenSession(String id)
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
    sLogger.debug("KasqConnection::authenticate() - IN");
    
    boolean result = false;
    try
    {
      KasqMessage authRequest = new KasqMessage();
      authRequest.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Authenticate);
      
      authRequest.setStringProperty(IKasqConstants.cPropertyUserName, userName);
      authRequest.setStringProperty(IKasqConstants.cPropertyPassword, password);
      
      boolean admin = false;
      if (userName.equals("admin"))
        admin = true;
      authRequest.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, admin);
      
      sLogger.debug("KasqConnection::authenticate() - Sending authenticate request via message: " + authRequest.toPrintableString(0));
      IPacket response = mMessenger.sendAndReceive(authRequest);
      if (response.getPacketClassId() == PacketHeader.cClassIdKasq)
      {
        IKasqMessage authResponse = (IKasqMessage)response;
        int responseCode = authResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
        if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
        {
          result = true;
          mPriviliged = admin;
        }
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqConnection::authenticate() - Exception caught: ", e);
    }
    
    sLogger.debug("KasqConnection::authenticate() - OUT, Result=" + result);
    return result;
  }
  
  /***************************************************************************************************************
   * Send a message to the KAS/Q server by calling the messenger's send() method.
   * If the message is a Shutdown request, call the halt() function instead
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
   * Send a message to the KAS/Q server and get a reply by calling the messenger's sendAndReceive() method.
   * 
   * @param message the message to be sent
   * 
   * @return the reply
   * 
   * @throws JMSException if an I/O exception occurs 
   */
  synchronized IKasqMessage internalSendAndReceive(IKasqMessage message) throws JMSException
  {
    IKasqMessage reply = null;
    try
    {
      IPacket packet = mMessenger.sendAndReceive(message);
      if (packet.getPacketClassId() != PacketHeader.cClassIdKasq)
      {
        throw new JMSException("Invalid reply from KAS/Q server", "Expected packet ID=[" + PacketHeader.cClassIdKasq + "], Actual=[" + packet.getPacketClassId() + "]");
      }
      reply = (IKasqMessage)packet;
    }
    catch (Throwable e)
    {
      throw new JMSException("Failed to send message", e.getMessage());
    }
    return reply;
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
