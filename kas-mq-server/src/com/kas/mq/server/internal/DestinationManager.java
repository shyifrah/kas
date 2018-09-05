package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqDestination;

/**
 * A {@link DestinationManager} is an object representing a manager of destinations
 * 
 * @author Pippo
 * 
 * @see com.kas.mq.impl.MqDestination
 */
public class DestinationManager extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * The name of this manager
   */
  private String mName;
  
  /**
   * The host (name or IP address) on which this manager is running
   */
  private String mHost;
  
  /**
   * The port number to which the manager listens for new connections
   */
  private int mPort;
  
  /**
   * Construct a {@link DestinationManager} object specifying the name, host and port
   * 
   * @param name The name of this manager object
   * @param host The host on which the manager is running
   * @param port The port to which the manager listens
   */
  public DestinationManager(String name, String host, int port)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mName = name;
    mHost = host;
    mPort = port;
  }
  
  /**
   * Get the object's string representation
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
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
