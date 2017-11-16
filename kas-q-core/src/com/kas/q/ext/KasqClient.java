package com.kas.q.ext;

import javax.jms.ConnectionFactory;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnectionFactory;
import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;

public class KasqClient extends AKasObject implements IInitializable
{
  private ILogger   mLogger;
  private boolean   mInitialized;
  
  private KasqConnectionFactory mConnectionFactory;
  
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
      {
        return false;
      }
      
      mLogger = LoggerFactory.getLogger(this.getClass());
      mLogger.info("KasqClient initialized successfully");
      
      boolean verified = verify(mHost, mPort);
      if (!verified)
      {
        return false;
      }
      
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
      MainConfiguration.getInstance().term();
      ThreadPool.shutdownNow();
      
      mLogger.info("KasqClient terminated");
    }
    
    return true;
  }

  /***************************************************************************************************************
   * Gets {@code ConnectionFactory}.
   * If the {@code ConnectionFactory} was not created, create it and then return it.
   * 
   * @return the connection factory
   */
  public ConnectionFactory getFactory()
  {
    ConnectionFactory factory = null;
    if (mInitialized)
    {
      if (mConnectionFactory == null)
      {
        mConnectionFactory = new KasqConnectionFactory(mHost, mPort);
      }
      factory = mConnectionFactory;
    }
    return factory;
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
  
  /***************************************************************************************************************
   * 
   */
  public KasqQueue locateQueue(String name)
  {
    KasqQueue queue = null;
    return queue;
  }
  
  /***************************************************************************************************************
   * 
   */
  public KasqTopic locateTopic(String name)
  {
    KasqTopic topic = null;
    return topic;
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
