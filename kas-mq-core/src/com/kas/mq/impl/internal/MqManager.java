package com.kas.mq.impl.internal;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.typedef.QueueMap;

/**
 * A {@link MqManager} is an object representing a manager of destinations
 * 
 * @author Pippo
 */
public class MqManager extends AKasObject
{
  /**
   * Logger
   */
  protected ILogger mLogger;
  
  /**
   * The name of this manager
   */
  protected String mName;
  
  /**
   * The host (name or IP address) on which this manager is running
   */
  protected String mHost;
  
  /**
   * The port number to which the manager listens for new connections
   */
  protected int mPort;
  
  /**
   * Is {@link MqManager} initialized
   */
  protected boolean mInitialized;
  
  /**
   * Map of all managed queues
   */
  protected QueueMap mQueues;
  
  /**
   * Construct a {@link MqManager} object specifying the name, host and port
   * 
   * @param name The name of this manager object
   * @param host The host on which the manager is running
   * @param port The port to which the manager listens
   */
  public MqManager(String name, String host, int port)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mName = name;
    mHost = host;
    mPort = port;
    mQueues = new QueueMap();
  }
  
  /**
   * Get the {@link MqManager} name
   * 
   * @return the {@link MqManager} name
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Get the {@link MqManager} host name or IP address
   * 
   * @return the {@link MqManager} host name or IP address
   */
  public String getHost()
  {
    return mHost;
  }
  
  /**
   * Get the port on which the {@link MqManager} listens
   * 
   * @return the port on which the {@link MqManager} listens
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * {@link MqManager} initialization
   */
  protected void init()
  {
    mInitialized = true;
  }
  
  /**
   * {@link MqManager} termination
   */
  protected void term()
  {
  }
  
  /**
   * {@link MqManager} termination
   */
  public boolean isInitialized()
  {
    return mInitialized;
  }
  
  /**
   * Get the object's string representation: <name>@<host>:<port>
   * 
   * @return the string representation
   */
  public String toString()
  {
    return new StringBuilder().append(mName).append('@').append(mHost).append(':').append(mPort).toString();
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Name=").append(mName).append('\n')
      .append(pad).append("  Host=").append(mHost).append('\n')
      .append(pad).append("  Port=").append(mPort).append('\n')
      .append(pad).append("  Queues=(").append('\n')
      .append(mQueues.toPrintableString(level+2))
      .append(pad).append("  )").append('\n')
      .append(pad).append(")");
    return sb.toString();
  }
}
