package com.kas.q.ext;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnection;
import com.kas.q.KasqConnectionFactory;
import com.kas.q.KasqQueue;
import com.kas.q.KasqQueueConnectionFactory;
import com.kas.q.KasqSession;
import com.kas.q.KasqTopic;

public class KasqClient extends AKasObject implements IInitializable
{
  protected ILogger mLogger;
  private boolean   mInitialized;
  
  private KasqConnectionFactory mConnectionFactory;
  private KasqQueueConnectionFactory mQueueConnectionFactory;
  
  private KasqConnection mClientConnection;
  private KasqSession    mClientSession;
  private boolean        mSessionInitialized;
  
  private String    mHost;
  private int       mPort;
  
  /***************************************************************************************************************
   * Construct KasQClient object
   * 
   * @param host hostname or IP-address of remote KasQ server
   * @param port port number on which the KasQ server listens for new connections
   */
  public KasqClient(String host, int port)
  {
    mHost = host;
    mPort = port;
    mConnectionFactory = null;
    mQueueConnectionFactory = null;
    mClientConnection = null;
    mClientSession = null;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init()
  {
    if (!mInitialized)
    {
      MainConfiguration mainConfig = MainConfiguration.getInstance();
      mainConfig.init();
      if (!mainConfig.isInitialized())
        return false;
      
      mLogger = LoggerFactory.getLogger(this.getClass());
      mLogger.info("KasqClient initialized successfully");
      
      boolean verified = verify(mHost, mPort);
      if (!verified)
        return false;
      
      mConnectionFactory = new KasqConnectionFactory(mHost, mPort);
      mQueueConnectionFactory = new KasqQueueConnectionFactory(mHost, mPort);
      
      mInitialized = true;
    }
    
    return true;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean term()
  {
    if (mInitialized)
    {
      mLogger.info("KasqClient terminates");
      
      mConnectionFactory.shutdown();
      mQueueConnectionFactory.shutdown();
      
      MainConfiguration.getInstance().term();
      ThreadPool.shutdownNow();
    }
    
    return true;
  }

  /***************************************************************************************************************
   * Gets {@code ConnectionFactory}.<br>
   * If the {@code ConnectionFactory} was not created, create it and then return it.
   * 
   * @return the connection factory
   * 
   * @throws JMSException if the KasqClient was not initialized
   */
  public ConnectionFactory getConnectionFactory() throws JMSException
  {
    if (!mInitialized)
    {
      throw new JMSException("KasqClient not initialized");
    }
    return mConnectionFactory;
  }
  
  /***************************************************************************************************************
   * Gets {@code QueueConnectionFactory}.<br>
   * If the {@code QueueConnectionFactory} was not created, create it and then return it.
   * 
   * @return the queue connection factory
   * 
   * @throws JMSException if the KasqClient was not initialized
   */
  public QueueConnectionFactory getQueueConnectionFactory() throws JMSException
  {
    if (!mInitialized)
    {
      throw new JMSException("KasqClient not initialized");
    }
    return mQueueConnectionFactory;
  }
  
  /***************************************************************************************************************
   * Verifies connection parameters
   * 
   * @param host hostname or IP-address of remote host
   * @param port port on which remote host listens for new connections
   * 
   * @return true if connection parameters were successfully verified
   */
  private boolean verify(String host, int port)
  {
    mLogger.debug("KasqClient::setup() - IN, Host=[" + (host == null ? "null" : host) + "]; Port=[" + port + "]");
    
    boolean verified = true;
    
    // make sure a valid port was specified
    if ((verified) && (port <= 0))
    {
      mLogger.error("Invalid port number (" + port + "). Quitting...");
      verified = false;
    }
    
    // make sure a valid host was specified
    if ((verified) && ((host == null) || (host.trim().length() == 0)))
    {
      mLogger.error("Invalid host name (" + (host == null ? "null" : host) + "). Quitting...");
      verified = false;
    }
    
    mLogger.debug("KasqClient::setup() - OUT, Result=" + verified);
    return verified;
  }
  
  /****************************************************************************************************************
   * Locate a queue in KAS/Q server
   * 
   * @param name the name of the queue
   * 
   * @return the {@code KasqQueue} object of the queue, or null if client failed to locate it
   * 
   * @throws JMSException if client's session initialization failed
   */
  public KasqQueue locateQueue(String name) throws JMSException
  {
    initClientSession();
    if (!mSessionInitialized)
    {
      throw new JMSException("Cannot locate queue with name " + name + ". Client session failed initialization");
    }
    
    return (KasqQueue)mClientSession.locateQueue(name);
  }
  
  /***************************************************************************************************************
   * Locate a topic in KAS/Q server
   * 
   * @param name the name of the topic
   * 
   * @return the {@code KasqTopic} object of the topic, or null if client failed to locate it
   * 
   * @throws JMSException if client's session initialization failed
   */
  public KasqTopic locateTopic(String name) throws JMSException
  {
    initClientSession();
    if (!mSessionInitialized)
    {
      throw new JMSException("Cannot locate queue with name " + name + ". Client session failed initialization");
    }
    
    return (KasqTopic)mClientSession.locateTopic(name);
  }
  
  
  /***************************************************************************************************************
   * Returns the admin queue defined in the KAS/Q server to which this client is connected
   * 
   * @return the admin queue 
   * 
   * @throws JMSException if client's session initialization failed
   */
  public KasqQueue getAdminQueue() throws JMSException
  {
    initClientSession();
    if (!mSessionInitialized)
    {
      throw new JMSException("Cannot get admin queue. Client session failed initialization");
    }
    
    return mClientConnection.getAdminQueue();
  }
  
  /***************************************************************************************************************
   * Initialize the client's Session and Connection which are used for locating resources in the KAS/Q server.
   * 
   * @throws JMSException if connection or session creation are failed
   */
  private void initClientSession() throws JMSException
  {
    if (!mSessionInitialized)
    {
      mClientConnection = (KasqConnection)mConnectionFactory.createConnection("admin", "admin");
      mClientSession = (KasqSession)mClientConnection.createSession();
      mSessionInitialized = true;
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
      .append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  ConnectionFactory=(").append(mConnectionFactory.toPrintableString(level + 2)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
