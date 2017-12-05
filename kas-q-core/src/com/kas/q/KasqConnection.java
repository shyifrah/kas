package com.kas.q;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.KasqMessageFactory;
import com.kas.q.ext.KasqReceiverTask;
import com.kas.q.requests.AuthRequest;
import com.kas.q.requests.MetaRequest;

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
  
  protected String  mUserName = null;
  protected boolean mPriviliged = false;
  
  protected List<KasqSession> mSessions;
  protected Map<String, KasqQueue> mTempQueues;
  protected Map<String, KasqTopic> mTempTopics;
  
  protected ConnectionMetaData mMetaData;
  
  protected IMessenger mMessenger;
  protected KasqReceiverTask mReceiverTask;
  
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
      mUserName = userName;
      
      mMessenger = MessengerFactory.create(host, port, new KasqMessageFactory());
      mClientId = "CLNT" + UniqueId.generate().toString();
      
      mSessions   = new ArrayList<KasqSession>();
      mTempQueues = new ConcurrentHashMap<String, KasqQueue>();
      mTempTopics = new ConcurrentHashMap<String, KasqTopic>();
      
      mMetaData = null;
      
      mReceiverTask = new KasqReceiverTask(mMessenger, mTempQueues);
    }
    catch (IOException e)
    {
      throw new JMSException("Connection creation failed", e.getMessage());
    }
    
    boolean authenticated = internalAuthenticate(userName, password);
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
    mReceiverTask.start();
    sLogger.diag("KasqConnection::start() - OUT");
  }

  /***************************************************************************************************************
   *  
   */
  public void stop()
  {
    sLogger.diag("KasqConnection::stop() - IN");
    try
    {
      mMessenger.shutdownInput();
    }
    catch (Throwable e) {}
    
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
    if (mMetaData == null)
    {
      ConnectionMetaData metaData = internalGetMetaData();
      if (metaData != null)
        mMetaData = metaData;
      else
        throw new JMSException("Failed to get KAS/Q meta data");
    }
    return mMetaData;
  }

  /***************************************************************************************************************
   *  
   */
  public ExceptionListener getExceptionListener() throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Connection.getExceptionListener()");
  }

  /***************************************************************************************************************
   *  
   */
  public void setExceptionListener(ExceptionListener listener) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Connection.setExceptionListener(ExceptionListener)");
  }

  /***************************************************************************************************************
   * Order of cleanup:
   * 1. Stop the receiver task because it is using the mTempQueuesMap.
   * 2. Close all sessions (which will trigger the closure of all {@code MessageConsumers} and {@code MessageProducers}).
   * 3. Clearing the Sessions list
   * 4. Clear the temporary queues map
   * 5. Clear the temporary topics map
   * 6. Perform {@code Messenger} cleanup
   */
  public void close() throws JMSException
  {
    try
    {
      stop();
      
      // call close() method on all sessions
      // when closing a session, it should close all consumers and producers
      synchronized (mSessions)
      {
        for (KasqSession sess : mSessions)
          sess.close();
        mSessions.clear();
      }
      
      // delete all temporary destinations
      mTempQueues.clear();
      mTempTopics.clear();
      
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
    // TODO: implement
    throw new JMSException("Unsupported method: Connection.createConnectionConsumer(destination, String, ServerSessionPool, int)");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Connection.createSharedConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: Connection.createDurableConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    // TODO: implement
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
  private boolean internalAuthenticate(String userName, String password) throws JMSException
  {
    sLogger.debug("KasqConnection::internalAuthenticate() - IN");
    
    boolean result = false;
    try
    {
      AuthRequest authRequest = new AuthRequest(userName, password);
      IKasqMessage requestMessage = authRequest.createRequestMessage();
      
      sLogger.debug("KasqConnection::internalAuthenticate() - Sending authenticate request via message: " + authRequest.toPrintableString(0));
      IKasqMessage authResponse = internalSendAndReceive(requestMessage);
      
      sLogger.debug("KasqConnection::internalAuthenticate() - Got response: " + authResponse.toPrintableString(0));
      int responseCode = authResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
      if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
      {
        result = true;
        mPriviliged = authRequest.isAdmin();
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqConnection::internalAuthenticate() - Exception caught: ", e);
    }
    
    sLogger.debug("KasqConnection::internalAuthenticate() - OUT, Result=" + result);
    return result;
  }
  
  /***************************************************************************************************************
   * Create a temporary destination
   * 
   * @param name the name of the destination to create
   * @param type the destination type
   * 
   * @return the created destination
   * 
   * @throws JMSException 
   */
  IKasqDestination internalCreateTemporaryDestination(String name, EDestinationType type) throws JMSException
  {
    sLogger.debug("KasqConnection::internalCreateTemporaryDestination() - IN");
    
    if (name == null)
      throw new JMSException("Failed to create destination: Invalid destination name: [" + StringUtils.asString(name) + "]");
    
    IKasqDestination dest = null;
    switch (type)
    {
      case cQueue:
        dest = new KasqQueue(name, "");
        mTempQueues.put(name, (KasqQueue)dest);
        break;
      case cTopic:
        dest = new KasqTopic(name, "");
        mTempTopics.put(name, (KasqTopic)dest);
        break;
      default:
        throw new JMSException("Failed to create destination: Invalid destination type: [" + type + "]");
    }
    
    sLogger.debug("KasqConnection::internalCreateTemporaryDestination() - OUT, Result=" + StringUtils.asString(dest));
    return dest;
  }
  
  /***************************************************************************************************************
   * Delete a temporary destination
   * 
   * @param name the name of the destination to delete
   * @param type the destination type
   * 
   * @throws JMSException 
   */
  void internalDeleteTemporaryDestination(String name, EDestinationType type) throws JMSException
  {
    sLogger.debug("KasqConnection::internalDeleteTemporaryDestination() - IN");
    
    if (name == null)
      throw new JMSException("Failed to delete destination: Invalid destination name: [" + StringUtils.asString(name) + "]");
    
    IKasqDestination dest = null;
    switch (type)
    {
      case cQueue:
        mTempQueues.remove(name);
        break;
      case cTopic:
        mTempTopics.remove(name);
        break;
      default:
        throw new JMSException("Failed to delete destination: Invalid destination type: [" + type.toString() + "]");
    }
    
    sLogger.debug("KasqConnection::internalDeleteTemporaryDestination() - OUT, Result=" + StringUtils.asString(dest));
  }
  
  /***************************************************************************************************************
   * Query the KAS/Q server for the Meta Data
   * 
   * @return the {@code ConnectionMetaData} returned by the KAS/Q server
   * 
   * @throws JMSException 
   */
  private ConnectionMetaData internalGetMetaData() throws JMSException
  {
    sLogger.debug("KasqConnection::internalGetMetaData() - IN");
    
    ConnectionMetaData metaData = null;
    
    try
    {
      MetaRequest metaRequest = new MetaRequest();
      IKasqMessage requestMessage = metaRequest.createRequestMessage();
      
      sLogger.debug("KasqConnection::internalGetMetaData() - Sending metadata request via message: " + requestMessage.toPrintableString(0));
      IKasqMessage metaResponse = internalSendAndReceive(requestMessage);
      
      sLogger.debug("KasqConnection::internalGetMetaData() - Got response: " + metaResponse.toPrintableString(0));
      int responseCode = metaResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
      if (responseCode == IKasqConstants.cPropertyResponseCode_Okay)
      {
        metaData = (ConnectionMetaData)metaResponse.getObjectProperty(IKasqConstants.cPropertyMetaData);
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqConnection::internalGetMetaData() - Exception caught: ", e);
    }
    
    sLogger.debug("KasqConnection::internalGetMetaData() - OUT, Result=" + StringUtils.asString(metaData));
    return metaData;
  }
  
  /***************************************************************************************************************
   * Send a message to the KAS/Q server by calling the messenger's send() method.<br>
   * Prior to handing the message to the Messenger, we set some JMSX properties, as required by JMS spec.
   * 
   * @param message the message to be sent
   * 
   * @throws JMSException if an I/O exception occurs 
   */
  synchronized void internalSend(IKasqMessage message) throws JMSException
  {
    // setting some mandatory and optional properties
    message.setStringProperty("JMSXUserID", mUserName);
    message.setIntProperty("JMSXDeliveryCount", 1);
    
    // actual send
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
   * Send a message to the KAS/Q server and get a reply by calling the messenger's sendAndReceive() method.<br>
   * Prior to handing the message to the Messenger, we set some JMSX properties, as required by JMS spec.
   * 
   * @param message the message to be sent
   * 
   * @return the reply
   * 
   * @throws JMSException if an I/O exception occurs 
   */
  synchronized IKasqMessage internalSendAndReceive(IKasqMessage message) throws JMSException
  {
    // setting some mandatory and optional properties 
    message.setStringProperty("JMSXUserID", mUserName);
    message.setIntProperty("JMSXDeliveryCount", 1);
    
    // actual send
    IPacket packet;
    try
    {
      packet = mMessenger.sendAndReceive(message);
    }
    catch (Throwable e)
    {
      throw new JMSException("Failed to send message", e.getMessage());
    }
    
    // verify valid response
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
