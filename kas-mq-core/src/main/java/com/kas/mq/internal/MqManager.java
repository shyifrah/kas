package com.kas.mq.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.typedef.StringList;
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
  protected Logger mLogger;
  
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
   * Is {@link MqManager} active
   */
  protected boolean mActive = false;
  
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
    mLogger = LogManager.getLogger(getClass());
    mName = name;
    mHost = host;
    mPort = port;
    mQueues = new QueueMap();
  }
  
  /**
   * Get the {@link MqManager} name
   * 
   * @return
   *   the {@link MqManager} name
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Get the {@link MqManager} host name or IP address
   * 
   * @return
   *   the {@link MqManager} host name or IP address
   */
  public String getHost()
  {
    return mHost;
  }
  
  /**
   * Get the port on which the {@link MqManager} listens
   * 
   * @return
   *   the port on which the {@link MqManager} listens
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * {@link MqManager} activation
   */
  public void activate()
  {
    mActive = true;
  }
  
  /**
   * {@link MqManager} deactivation
   */
  public void deactivate()
  {
    mActive = false;
  }
  
  /**
   * Get an indication if {@link MqManager} is active
   * 
   * @return
   *   {@code true} if {@link MqManager} is active, {@code false} otherwise
   */
  public boolean isActive()
  {
    return mActive;
  }
  
  /**
   * Query queues
   * 
   * @param name
   *   The queue name/prefix.
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a properties object that holds the queried data
   */
  public StringList queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.trace("MqManager::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    StringList qlist = new StringList();
    for (MqQueue queue : mQueues.values())
    {
      boolean include = false;
      if (prefix)
        include = queue.getName().startsWith(name);
      else
        include = queue.getName().equals(name);
      
      mLogger.trace("MqManager::queryQueue() - Checking if current queue [" + queue.getName() + "] matches query: " + include);
      if (include)
      {
        qlist.add(queue.queryResponse(all));
      }
    }
    
    mLogger.trace("MqManager::queryQueue() - OUT, Returns=" + qlist.size() + " queues");
    return qlist;
  }
  
  /**
   * Get the object's string representation
   * 
   * @return
   *   the string representation
   */
  public String toString()
  {
    return new StringBuilder().append(mName).append('@').append(mHost).append(':').append(mPort).toString();
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Name=").append(mName).append('\n')
      .append(pad).append("  Host=").append(mHost).append('\n')
      .append(pad).append("  Port=").append(mPort).append('\n')
      .append(pad).append("  Active=").append(mActive).append('\n')
      .append(pad).append("  Queues=(").append('\n')
      .append(mQueues.toPrintableString(level+2))
      .append(pad).append("  )").append('\n')
      .append(pad).append(")");
    return sb.toString();
  }
}
