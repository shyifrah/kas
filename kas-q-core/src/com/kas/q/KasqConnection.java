package com.kas.q;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
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
  
  protected boolean mPriviliged = false;
  
  protected List<KasqSession> mSessions;
  protected List<KasqQueue>  mTempQueues;
  protected List<KasqTopic>  mTempTopics;
  
  protected IMessenger mMessenger;
  
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
      mMessenger = MessengerFactory.create(host, port, new KasqMessageFactory());
      mClientId = "CLNT" + UniqueId.generate().toString();
      
      mSessions = new ArrayList<KasqSession>();
      mTempQueues = new ArrayList<KasqQueue>();
      mTempTopics = new ArrayList<KasqTopic>();
    }
    catch (IOException e)
    {
      throw new JMSException("Connection creation failed", e.getMessage());
    }
    
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
    sLogger.diag("KasqConnection::start() - IN");
    mStarted = true;
    sLogger.diag("KasqConnection::start() - OUT");
  }

  /***************************************************************************************************************
   *  
   */
  public void stop()
  {
    sLogger.diag("KasqConnection::stop() - IN");
    mStarted = false;
    sLogger.diag("KasqConnection::stop() - OUT");
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
    KasqSession sess = new KasqSession(this);
    synchronized (mSessions)
    {
      mSessions.add(sess);
    }
    return sess;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    KasqSession sess = new KasqSession(this, transacted, acknowledgeMode);
    synchronized (mSessions)
    {
      mSessions.add(sess);
    }
    return sess;
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession(int sessionMode) throws JMSException
  {
    KasqSession sess = new KasqSession(this, sessionMode);
    synchronized (mSessions)
    {
      mSessions.add(sess);
    }
    return sess;
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
      // call close() method on all sessions
      // when closing a session, it should close all consumers and producers
      synchronized (mSessions)
      {
        for (KasqSession sess : mSessions)
          sess.close();
      }
      
      // delete all temporary destinations
      synchronized (mTempQueues)
      {
        for (KasqQueue queue : mTempQueues)
          queue.delete();
      }
      
      synchronized (mTempTopics)
      {
        for (KasqTopic topic : mTempTopics)
          topic.delete();
      }
      
      stop();
      
      // release all allocated resources (?)
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
      IKasqMessage authResponse = internalSendAndReceive(authRequest);
      
      sLogger.debug("KasqConnection::authenticate() - Got response: " + authResponse.toPrintableString(0));
      int responseCode = authResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
      if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
      {
        result = true;
        mPriviliged = admin;
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
   * Create a temporary destination
   * 
   * @param name the name of the destination to create
   * @param type an integer representing the type of the destination: 1 - queue, 2 - topic
   * 
   * @return the created destination
   * 
   * @throws JMSException 
   */
  IKasqDestination internalCreateTemporaryDestination(String name, int type) throws JMSException
  {
    sLogger.debug("KasqConnection::internalCreateTemporaryDestination() - IN");
    
    if (name == null)
      throw new JMSException("Failed to create destination: Invalid destination name: [" + StringUtils.asString(name) + "]");
    
    IKasqDestination dest = null;
    if (type == IKasqConstants.cPropertyDestinationType_Queue)
    {
      dest = new KasqQueue(name, "");
      synchronized (mTempQueues)
      {
        mTempQueues.add((KasqQueue)dest);
      }
    }
    else
    if (type == IKasqConstants.cPropertyDestinationType_Topic)
    {
      dest = new KasqTopic(name, "");
      synchronized (mTempTopics)
      {
        mTempTopics.add((KasqTopic)dest);
      }
    }
    else
    {
      throw new JMSException("Failed to create destination: Invalid destination type: [" + type + "]");
    }
    
    sLogger.debug("KasqConnection::internalCreateTemporaryDestination() - OUT, Result=" + StringUtils.asString(dest));
    return dest;
  }
  
  /***************************************************************************************************************
   * Send a message to the KAS/Q server by calling the messenger's send() method.
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
    IPacket packet;
    try
    {
      packet = mMessenger.sendAndReceive(message);
    }
    catch (Throwable e)
    {
      throw new JMSException("Failed to send message", e.getMessage());
    }
    
    if (packet.getPacketClassId() != PacketHeader.cClassIdKasq)
    {
      throw new JMSException("Invalid reply from KAS/Q server", "Expected packet ID=[" + PacketHeader.cClassIdKasq + "], Actual=[" + packet.getPacketClassId() + "]");
    }
    
    return (IKasqMessage)packet;
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
      .append(pad).append("  Sessions=(")
      .append(StringUtils.asPrintableString(mSessions, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  TempQueues=(")
      .append(StringUtils.asPrintableString(mTempQueues, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  TempTopics=(")
      .append(StringUtils.asPrintableString(mTempTopics, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
