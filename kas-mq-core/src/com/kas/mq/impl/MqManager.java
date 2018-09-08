package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.types.QueueMap;

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
   * Map of all managed queues
   */
  private QueueMap mQueues;
  
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
   * Add a queue to the local queues map
   * 
   * @param queue The {@link MqQueue} to add
   */
  public void createQueue(MqQueue queue)
  {
    mLogger.debug("MqManager::createQueue() - IN, Name=" + queue.getName());
    
    String name = queue.getName();
    MqQueue oldq = mQueues.put(name, queue);
    if (oldq != null)
      mQueues.put(name, oldq);
    
    mLogger.debug("MqManager::createQueue() - Queue with name " + name + (oldq == null ? " was successfully created" : " already exist"));
    mLogger.debug("MqManager::createQueue() - OUT");
  }
  
  /**
   * Remove a queue from the local queues map
   * 
   * @param name The {@link MqQueue} name to remove
   */
  public void removeQueue(String name)
  {
    mLogger.debug("MqManager::removeQueue() - IN, Name=" + name);
    
    MqQueue oldq = mQueues.remove(name);
    
    mLogger.debug("MqManager::removeQueue() - Queue with name " + name + (oldq != null ? " was successfully removed" : " does not exist"));
    mLogger.debug("MqManager::removeQueue() - OUT");
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
