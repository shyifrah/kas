package com.kas.mq.server.repo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.internal.MqManager;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.internal.MqRemoteQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.MqConfiguration;

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
  private MqLocalManager mLocalManager;
  
  /**
   * The local queue manager
   */
  private Map<String, MqRemoteManager> mRemoteManagersMap;
  
  /**
   * Construct the server repository object.
   * 
   * @param config The {@link MqConfiguration}
   */
  public ServerRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mLocalManager = new MqLocalManager(mConfig.getManagerName(), mConfig.getPort(), mConfig.getDeadQueueName());
    mRemoteManagersMap = new ConcurrentHashMap<String, MqRemoteManager>();
    
    mConfig.register(this);
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
    mLocalManager.activate();
    if (mLocalManager.isActive())
    {
      createPredefinedQueues();
      success = true;
    }
    
    for (Map.Entry<String, NetworkAddress> entry : mConfig.getRemoteManagers().entrySet())
    {
      String name = entry.getKey();
      NetworkAddress addr = entry.getValue();
      MqRemoteManager mgr = new MqRemoteManager(name, addr.getHost(), addr.getPort());
      
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
    
    mLocalManager.deactivate();
    
    mLogger.debug("ServerRepository::term() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Configuration has been refreshed.<br>
   * <br>
   * If KAS/MQ server's configuration has been refreshed, it means that the repository
   * should re-create (if necessary) the list of pre-defined queues.
   */
  public void refresh()
  {
    mLogger.debug("ServerRepository::refresh() - IN");
    
    createPredefinedQueues();
    
    mLogger.debug("ServerRepository::refresh() - OUT");
  }
  
  /**
   * Create a {@link MqLocalQueue} object with the specified {@code name} and {@code threshold}.
   * 
   * @param name The name of the queue
   * @param thoreshold The queue threshold
   * @param perm Is the queue should be defined as permanent
   * @return the {@link MqLocalQueue} object created
   * 
   * @see com.kas.mq.server.IRepository#defineLocalQueue(String, int)
   */
  public MqLocalQueue defineLocalQueue(String name, int threshold, boolean perm)
  {
    mLogger.debug("ServerRepository::defineLocalQueue() - IN, Name=" + name + ", Threshold=" + threshold);
    MqLocalQueue queue = mLocalManager.defineQueue(name, threshold, perm);
    mLogger.debug("ServerRepository::defineLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Add a {@link MqRemoteQueue} object to a specific {@link MqRemoteManager}
   * 
   * @param qmgr The name of the KAS/MQ server
   * @param queue The name of queue
   * @return the {@link MqRemoteQueue} object created
   * 
   * @see com.kas.mq.server.IRepository#defineRemoteQueue(String, String)
   */
  public MqRemoteQueue defineRemoteQueue(String qmgr, String queue)
  {
    mLogger.debug("ServerRepository::defineRemoteQueue() - IN, Qmgr=" + qmgr + ", Queue=" + queue);
    
    MqRemoteQueue mqrq = null;
    MqRemoteManager rqmgr = getRemoteManager(qmgr);
    if (rqmgr != null)
      mqrq = rqmgr.addQueue(queue);
      
    mLogger.debug("ServerRepository::defineRemoteQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return mqrq;
  }
  
  /**
   * Delete a {@link MqRemoteQueue} object from a specific {@link MqRemoteManager}.
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
   * Delete a {@link MqRemoteQueue} object from a specific {@link MqRemoteManager}.
   * 
   * @param qmgr The name of the KAS/MQ server
   * @param queue The name of queue
   * @return the {@link MqRemoteQueue} object deleted
   * 
   * @see com.kas.mq.server.IRepository#deleteRemoteQueue(String, String)
   */
  public MqRemoteQueue deleteRemoteQueue(String qmgr, String queue)
  {
    mLogger.debug("ServerRepository::deleteRemoteQueue() - IN, Qmgr=" + qmgr + ", Queue=" + queue);
    MqRemoteQueue mqrq = null;
    MqRemoteManager rqmgr = getRemoteManager(qmgr);
    if (rqmgr != null)
      mqrq = rqmgr.removeQueue(queue);
    
    mLogger.debug("ServerRepository::deleteRemoteQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return mqrq;
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
   * Get a {@link MqRemoteQueue} object with the specified {@code name}.
   * 
   * @param name The name of the remote queue to be retrieved
   * @return the {@link MqRemoteQueue} object or {@code null} if {@code name} is {@code null}, or there's no queue with this name.
   * 
   * @see IRepository#getRemoteQueue(String)
   */
  public MqRemoteQueue getRemoteQueue(String name)
  {
    mLogger.debug("ServerRepository::getRemoteQueue() - IN, Name=" + name);
    MqRemoteQueue queue = null;
    for (Map.Entry<String, MqRemoteManager> entry : mRemoteManagersMap.entrySet())
    {
      String qmgrName = entry.getKey();
      MqRemoteManager qmgr = entry.getValue();
      
      mLogger.debug("ServerRepository::getRemoteQueue() - Check if remote KAS/MQ server " + qmgrName + " has a queue named " + name);
      queue = qmgr.getQueue(name);
      if (queue != null) break;
    }
    mLogger.debug("ServerRepository::getRemoteQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get a {@link MqQueue} object with the specified {@code name}.<br>
   * <br>
   * We search for a local queue with the specified name, and if we don't find, we look for a remote one.
   * 
   * @param name The name of the queue to be retrieved
   * @return the {@link MqRemoteQueue} object or {@code null} if {@code name} is {@code null}, or there's no queue with this name.
   */
  public MqQueue getQueue(String name)
  {
    mLogger.debug("ServerRepository::getQueue() - IN, Name=" + name);
    MqQueue queue = getLocalQueue(name);
    if (queue == null)
    {
      queue = getRemoteQueue(name);
    }
    
    mLogger.debug("ServerRepository::getQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get information regarding local queues whose name begins with the specified prefix.
   * 
   * @param name The queue name/prefix.
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   * 
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean, boolean)
   */
  public Properties queryLocalQueues(String name, boolean prefix, boolean all)
  {
    mLogger.debug("ServerRepository::queryLocalQueues() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    mLogger.debug("ServerRepository::queryLocalQueues() - Checking if LocalQueuesManager " + mLocalManager.getName() + " has results for this query...");
    Properties result = mLocalManager.queryQueue(name, prefix, all);
    
    mLogger.debug("ServerRepository::queryLocalQueues() - OUT, Returns=" + result.size() + " records");
    return result;
  }
  
  /**
   * Get information regarding remote queues whose name begins with the specified prefix.
   * 
   * @param name The queue name/prefix.
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   * 
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean, boolean)
   */
  public Properties queryRemoteQueues(String name, boolean prefix, boolean all)
  {
    mLogger.debug("ServerRepository::queryRemoteQueues() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    Properties result = new Properties();
    for (Map.Entry<String, MqRemoteManager> entry : mRemoteManagersMap.entrySet())
    {
      MqRemoteManager mgr = entry.getValue();
      mLogger.debug("ServerRepository::queryRemoteQueues() - Checking if RemoteQueuesManager " + mgr.getName() + " has results for this query...");
      Properties props = mgr.queryQueue(name, prefix, all);
      result.putAll(props);
    }
    
    mLogger.debug("ServerRepository::queryRemoteQueues() - OUT, Returns=" + result.size() + " records");
    return result;
  }
  
  /**
   * Get information regarding all queues whose name begins with the specified prefix.<br>
   * <br>
   * Note we add the local queues to the result so local queues will override the remote ones
   * if there are any duplicates.
   * 
   * @param locals When {@code true}, result of query will include only local queues
   * @param name The queue name/prefix.
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   * 
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean, boolean)
   */
  public Properties queryQueues(String name, boolean prefix, boolean all)
  {
    mLogger.debug("ServerRepository::queryQueues() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    Properties result = queryRemoteQueues(name, prefix, all);
    Properties locals = queryLocalQueues(name, prefix, all);
    result.putAll(locals);
    
    mLogger.debug("ServerRepository::queryQueues() - OUT, Returns=" + result.size() + " records");
    return result;
  }
  
  /**
   * Get {@link MqManager} by its name 
   * 
   * @param name The name of the {@link MqManager}
   * @return the {@link MqManager}
   * 
   * @see com.kas.mq.server.IRepository#getRemoteManager(String)
   */
  public MqRemoteManager getRemoteManager(String name)
  {
    return mRemoteManagersMap.get(name);
  }
  
  /**
   * Get all remote {@link MqRemoteManager managers} 
   * 
   * @return the {@link MqManager}
   * 
   * @see com.kas.mq.server.IRepository#getRemoteManagers()
   */
  public Collection<MqRemoteManager> getRemoteManagers()
  {
    return mRemoteManagersMap.values();
  }
  
  /**
   * Get the local {@link MqLocalManager} 
   * 
   * @return the local {@link MqLocalManager}
   * 
   * @see com.kas.mq.server.IRepository#getLocalManager()
   */
  public MqLocalManager getLocalManager()
  {
    return mLocalManager;
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
   * Create queues that are configured in configuration file
   */
  private void createPredefinedQueues()
  {
    mLogger.debug("ServerRepository::createPredefinedQueues() - IN");
    
    int total = 0, defined = 0;
    for (Map.Entry<String, Integer> entry : mConfig.getQueueDefinitions().entrySet())
    {
      ++total;
      String name = entry.getKey();
      int threshold = entry.getValue();
      MqQueue queue = getLocalQueue(name);
      if (queue == null)
      {
        defineLocalQueue(name, threshold, true);
        ++defined;
      }
    }
    
    mLogger.debug("ServerRepository::createPredefinedQueues() - Total pre-defined queues: " + total + ", defined: " + defined);
    
    mLogger.debug("ServerRepository::createPredefinedQueues() - OUT");
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
