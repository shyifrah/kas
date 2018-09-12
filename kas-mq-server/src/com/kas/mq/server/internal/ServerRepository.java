package com.kas.mq.server.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.server.IRepository;

/**
 * The server repository is the class that manages all KAS/MQ entities
 * 
 * @author Pippo
 */
public class ServerRepository extends AKasObject implements IRepository
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * KAS/MQ configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * The local queue manager
   */
  private LocalQueuesManager mLocalManager;
  
  /**
   * The local queue manager
   */
  private Map<String, RemoteQueuesManager> mRemoteManagersMap;
  
  /**
   * Construct the server repository object.
   * 
   * @param config The {@link MqConfiguration}
   */
  public ServerRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mLocalManager = new LocalQueuesManager(mConfig);
    mRemoteManagersMap = new ConcurrentHashMap<String, RemoteQueuesManager>();
  }
  
  /**
   * Initialize the server repository
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public boolean init()
  {
    mLogger.debug("ServerRepository::init() - IN");
    boolean success = false;
    
    mLogger.trace("ServerRepository::init() - Initializing repository...");
    mLocalManager.init();
    if (mLocalManager.isInitialized())
      success = true;
    
    for (Map.Entry<String, NetworkAddress> entry : mConfig.getRemoteManagers().entrySet())
    {
      String name = entry.getKey();
      NetworkAddress addr = entry.getValue();
      RemoteQueuesManager mgr = new RemoteQueuesManager(name, addr.getHost(), addr.getPort());
      mgr.init();
      
      mRemoteManagersMap.put(name, mgr);
    }
    
    mLogger.debug("ServerRepository::init() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Terminate the server repository
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public boolean term()
  {
    mLogger.debug("ServerRepository::term() - IN");
    boolean success = true;
    
    mLocalManager.term();
    
    mLogger.debug("ServerRepository::term() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Create a {@link MqLocalQueue} object with the specified {@code name} and {@code threshold}.
   * 
   * @param name The name of the queue
   * @param queue The queue threshold
   * @return the {@link MqLocalQueue} object created
   * 
   * @see com.kas.mq.server.IRepository#defineLocalQueue(String, int)
   */
  public MqLocalQueue defineLocalQueue(String name, int threshold)
  {
    mLogger.debug("ServerRepository::defineLocalQueue() - IN, Name=" + name + ", Threshold=" + threshold);
    MqLocalQueue queue = mLocalManager.defineQueue(name, threshold);
    mLogger.debug("ServerRepository::defineLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Remove a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be removed
   * @return the {@link MqLocalQueue} object removed
   * 
   * @see com.kas.mq.server.IRepository#deleteLocalQueue(String)
   */
  public MqLocalQueue deleteLocalQueue(String name)
  {
    mLogger.debug("ServerRepository::deleteLocalQueue() - IN, Name=" + name);
    MqLocalQueue queue = mLocalManager.deleteQueue(name);
    mLogger.debug("ServerRepository::deleteLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   * 
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean, boolean)
   */
  public Properties queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("ServerRepository::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    Properties props = mLocalManager.queryQueue(name, prefix, all);
    mLogger.debug("ServerRepository::queryQueue() - OUT, Returns=" + props.size() + " records");
    return props;
  }
  
  /**
   * Get a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name The name of the destination to be retrieved
   * @return the {@link MqLocalQueue} object or {@code null} if {@code name} is {@code null}, or there's no queue with this name.
   * 
   * @see IRepository#getLocalQueue(String)
   */
  public MqLocalQueue getLocalQueue(String name)
  {
    mLogger.debug("ServerRepository::getLocalQueue() - IN, Name=" + name);
    MqLocalQueue queue = mLocalManager.getQueue(name);
    mLogger.debug("ServerRepository::getLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Is a queue exists
   * 
   * @param name The name of the queue to be tested
   * @return {@code true} if a queue named {@code name} exists, {@code false} otherwise 
   * 
   * @see com.kas.mq.server.IRepository#isLocalQueueExist(String)
   */
  public boolean isLocalQueueExist(String name)
  {
    mLogger.debug("ServerRepository::isQueueExist() - IN, Name=" + name);
    
    boolean result = false;
    if ((name != null) && (getLocalQueue(name) != null))
      result = true;
    
    mLogger.debug("ServerRepository::isQueueExist() - OUT, Returns=" + result);
    return result;
  }
  
  /**
   * Get the {@link MqLocalQueue} object representing the dead queue
   * 
   * @return the {@link MqLocalQueue} object of the dead queue
   * 
   * @see com.kas.mq.server.IRepository#getDeadQueue()
   */
  public MqLocalQueue getDeadQueue()
  {
    return mLocalManager.getDeadQueue();
  }
  
  /**
   * Get a collection of all local queues
   * 
   * @return a collection of all local queues
   * 
   * @see com.kas.mq.server.IRepository#getLocalQueues()
   */
  public Collection<MqQueue> getLocalQueues()
  {
    return mLocalManager.getAll();
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
    sb.append(name()).append("(\n");
    ///
    /// TODO: COMPLETE
    ///
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
