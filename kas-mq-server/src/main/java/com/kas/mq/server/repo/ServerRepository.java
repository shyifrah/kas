package com.kas.mq.server.repo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.typedef.StringList;
import com.kas.mq.internal.EQueueDisp;
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
  private Logger mLogger;
  
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
   * @param config
   *   The {@link MqConfiguration}
   */
  public ServerRepository(MqConfiguration config)
  {
    mLogger = LogManager.getLogger(getClass());
    mConfig = config;
    mLocalManager = new MqLocalManager(mConfig.getManagerName(), mConfig.getPort(), mConfig.getDeadQueueName());
    mRemoteManagersMap = new ConcurrentHashMap<String, MqRemoteManager>();
    
    mConfig.register(this);
  }
  
  /**
   * Initialize the server repository
   * 
   * @return
   *   {@code true} if initialization completed successfully, {@code false} otherwise
   */
  public boolean init()
  {
    mLogger.trace("ServerRepository::init() - IN");
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
    
    mLogger.trace("ServerRepository::init() - OUT, Returns={}", success);
    return success;
  }
  
  /**
   * Terminate the server repository
   * 
   * @return
   *   {@code true} if termination completed successfully, {@code false} otherwise
   */
  public boolean term()
  {
    mLogger.trace("ServerRepository::term() - IN");
    boolean success = true;
    
    mLocalManager.deactivate();
    
    mLogger.trace("ServerRepository::term() - OUT, Returns={}", success);
    return success;
  }
  
  /**
   * Configuration has been refreshed.<br>
   * If KAS/MQ server's configuration has been refreshed, it means that the repository
   * should re-create (if necessary) the list of pre-defined queues.
   */
  public void refresh()
  {
    mLogger.trace("ServerRepository::refresh() - IN");
    
    createPredefinedQueues();
    
    mLogger.trace("ServerRepository::refresh() - OUT");
  }
  
  /**
   * Create a {@link MqLocalQueue} object with the specified {@code name} and {@code threshold}.
   * 
   * @param name
   *   The name of the queue
   * @param desc
   *   The queue description
   * @param thoreshold
   *   The queue threshold
   * @param disp
   *   The queue disposition
   * @return
   *   the {@link MqLocalQueue} object created
   */
  public MqLocalQueue defineLocalQueue(String name, String desc, int threshold, EQueueDisp disp)
  {
    mLogger.trace("ServerRepository::defineLocalQueue() - IN, Name={}, Threshold={}", name, threshold);
    MqLocalQueue queue = mLocalManager.defineQueue(name, desc, threshold, disp);
    mLogger.trace("ServerRepository::defineLocalQueue() - OUT, Returns=[{}]", queue);
    return queue;
  }    
  
  /**
   * Add a {@link MqRemoteQueue} object to a specific {@link MqRemoteManager}
   * 
   * @param qmgr
   *   The name of the KAS/MQ server
   * @param queue
   *   The name of queue
   * @return
   *   the {@link MqRemoteQueue} object created
   */
  public MqRemoteQueue defineRemoteQueue(String qmgr, String queue)
  {
    mLogger.trace("ServerRepository::defineRemoteQueue() - IN, Qmgr={}, Queue={}", qmgr, queue);
    
    MqRemoteQueue mqrq = null;
    MqRemoteManager rqmgr = getRemoteManager(qmgr);
    if (rqmgr != null)
      mqrq = rqmgr.addQueue(queue);
      
    mLogger.trace("ServerRepository::defineRemoteQueue() - OUT,  Returns=[{}]", queue);
    return mqrq;
  }    
  
  /**
   * alter a {@link MqLocalQueue} object with the specified {@code name} and {@code properties}.
   * 
   * @param name
   *   The name of the queue
   * @param qprops
   *   The queue properties we want to alter 
   * @return
   *   the {@link MqLocalQueue} object created
   */
  public MqLocalQueue alterLocalQueue(String name, Properties qprops)
  {
    mLogger.trace("ServerRepository::alterLocalQueue() - IN, Name={}", name);
    MqLocalQueue queue = mLocalManager.alterQueue(name, qprops);
    mLogger.trace("ServerRepository::alterLocalQueue() - OUT, Returns=[{}]", queue);
    return queue;
  }
  
  /**
   * Delete a {@link MqRemoteQueue} object from a specific {@link MqRemoteManager}.
   * 
   * @param name
   *   The name of the queue to be removed
   * @return
   *   the {@link MqLocalQueue} object removed
   */
  public MqLocalQueue deleteLocalQueue(String name)
  {
    mLogger.trace("ServerRepository::deleteLocalQueue() - IN, Name={}", name);
    MqLocalQueue queue = mLocalManager.deleteQueue(name);
    mLogger.trace("ServerRepository::deleteLocalQueue() - OUT,  Returns=[{}]", queue);
    return queue;
  }
  
  /**
   * Delete a {@link MqRemoteQueue} object from a specific {@link MqRemoteManager}.
   * 
   * @param qmgr
   *   The name of the KAS/MQ server
   * @param queue
   *   The name of queue
   * @return
   *   the {@link MqRemoteQueue} object deleted
   */
  public MqRemoteQueue deleteRemoteQueue(String qmgr, String queue)
  {
    mLogger.trace("ServerRepository::deleteRemoteQueue() - IN, Qmgr={}, Queue={}", qmgr, queue);
    MqRemoteQueue mqrq = null;
    MqRemoteManager rqmgr = getRemoteManager(qmgr);
    if (rqmgr != null)
      mqrq = rqmgr.removeQueue(queue);
    
    mLogger.trace("ServerRepository::deleteRemoteQueue() - OUT, Returns=[{}]", queue);
    return mqrq;
  }
  
  /**
   * Get a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name
   *   The name of the destination to be retrieved
   * @return
   *   the {@link MqLocalQueue} object or {@code null} if {@code name} is {@code null},
   *   or there's no queue with this name.
   */
  public MqLocalQueue getLocalQueue(String name)
  {
    mLogger.trace("ServerRepository::getLocalQueue() - IN, Name={}", name);
    MqLocalQueue queue = mLocalManager.getQueue(name);
    mLogger.trace("ServerRepository::getLocalQueue() - OUT, Returns=[{}]", queue);
    return queue;
  }
  
  /**
   * Get a {@link MqRemoteQueue} object with the specified {@code name}.
   * 
   * @param name
   *   The name of the remote queue to be retrieved
   * @return
   *   the {@link MqRemoteQueue} object or {@code null} if {@code name} is {@code null},
   *   or there's no queue with this name.
   */
  public MqRemoteQueue getRemoteQueue(String name)
  {
    mLogger.trace("ServerRepository::getRemoteQueue() - IN, Name={}", name);
    MqRemoteQueue queue = null;
    for (Map.Entry<String, MqRemoteManager> entry : mRemoteManagersMap.entrySet())
    {
      String qmgrName = entry.getKey();
      MqRemoteManager qmgr = entry.getValue();
      
      mLogger.trace("ServerRepository::getRemoteQueue() - Check if remote KAS/MQ server {} has a queue named {}", qmgrName, name);
      queue = qmgr.getQueue(name);
      if (queue != null) break;
    }
    mLogger.trace("ServerRepository::getRemoteQueue() - OUT, Returns=[{}]", queue);
    return queue;
  }
  
  /**
   * Get a {@link MqQueue} object with the specified {@code name}.<br>
   * We search for a local queue with the specified name, and if we don't find, we look for a remote one.
   * 
   * @param name
   *   The name of the queue to be retrieved
   * @return
   *   the {@link MqRemoteQueue} object or {@code null} if {@code name} is {@code null},
   *   or there's no queue with this name.
   */
  public MqQueue getQueue(String name)
  {
    mLogger.trace("ServerRepository::getQueue() - IN, Name={}", name);
    MqQueue queue = getLocalQueue(name);
    if (queue == null)
    {
      queue = getRemoteQueue(name);
    }
    
    mLogger.trace("ServerRepository::getQueue() - OUT, Returns=[{}]", queue);
    return queue;
  }
  
  /**
   * Get information regarding local queues whose name begins with the specified prefix.
   * 
   * @param name
   *   The queue name/prefix.
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a {@link StringList} object that holds the queried data
   */
  public StringList queryLocalQueues(String name, boolean prefix, boolean all)
  {
    mLogger.trace("ServerRepository::queryLocalQueues() - IN, Name={}, Prefix={}, All={}", name, prefix, all);
    
    mLogger.trace("ServerRepository::queryLocalQueues() - Checking if LocalQueuesManager {} has results for this query...", mLocalManager.getName());
    StringList result = mLocalManager.queryQueue(name, prefix, all);
    
    mLogger.trace("ServerRepository::queryLocalQueues() - OUT, Returns={} records", result.size());
    return result;
  }
  
  /**
   * Get information regarding remote queues whose name begins with the specified prefix.
   * 
   * @param name
   *   The queue name/prefix.
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a {@link StringList} object that holds the queried data
   */
  public StringList queryRemoteQueues(String name, boolean prefix, boolean all)
  {
    mLogger.trace("ServerRepository::queryRemoteQueues() - IN, Name={}, Prefix={}, All={}", name, prefix, all);
    
    StringList result = new StringList();
    for (Map.Entry<String, MqRemoteManager> entry : mRemoteManagersMap.entrySet())
    {
      MqRemoteManager mgr = entry.getValue();
      mLogger.trace("ServerRepository::queryRemoteQueues() - Checking if RemoteQueuesManager {} has results for this query...", mgr.getName());
      StringList list = mgr.queryQueue(name, prefix, all);
      result.addAll(list);
    }
    
    mLogger.trace("ServerRepository::queryRemoteQueues() - OUT, Returns={} records", result.size());
    return result;
  }
  
  /**
   * Get information regarding all queues whose name begins with the specified prefix.<br>
   * Note we add the local queues to the result so local queues will override the remote ones
   * if there are any duplicates.
   * 
   * @param locals
   *   When {@code true}, result of query will include only local queues
   * @param name
   *   The queue name/prefix.
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a {@link StringList} object that holds the queried data
   */
  public StringList queryQueues(String name, boolean prefix, boolean all)
  {
    mLogger.trace("ServerRepository::queryQueues() - IN, Name={}, Prefix={}, All={}", name, prefix, all);
    
    StringList result = queryRemoteQueues(name, prefix, all);
    StringList locals = queryLocalQueues(name, prefix, all);
    result.addAll(locals);
    
    mLogger.trace("ServerRepository::queryQueues() - OUT, Returns={} records", result.size());
    return result;
  }
  
  /**
   * Get {@link MqManager} by its name 
   * 
   * @param name
   *   The name of the {@link MqManager}
   * @return
   *   the {@link MqManager}
   */
  public MqRemoteManager getRemoteManager(String name)
  {
    return mRemoteManagersMap.get(name);
  }
  
  /**
   * Get all remote {@link MqRemoteManager managers} 
   * 
   * @return
   *   a collection of all {@link MqRemoteManager}
   */
  public Collection<MqRemoteManager> getRemoteManagers()
  {
    return mRemoteManagersMap.values();
  }
  
  /**
   * Get the local {@link MqLocalManager} 
   * 
   * @return
   *   the local {@link MqLocalManager}
   */
  public MqLocalManager getLocalManager()
  {
    return mLocalManager;
  }
  
  /**
   * Get the {@link MqLocalQueue} object representing the dead queue
   * 
   * @return
   *   the {@link MqLocalQueue} object of the dead queue
   */
  public MqLocalQueue getDeadQueue()
  {
    return mLocalManager.getDeadQueue();
  }
  
  /**
   * Get a collection of all local queues
   * 
   * @return
   *   a collection of all {@link MqLocalQueue}
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
    mLogger.trace("ServerRepository::createPredefinedQueues() - IN");
    
    int total = 0, defined = 0;
    for (Map.Entry<String, Integer> entry : mConfig.getQueueDefinitions().entrySet())
    {
      ++total;
      String name = entry.getKey();
      int threshold = entry.getValue();
      MqQueue queue = getLocalQueue(name);
      if (queue == null)
      {
        defineLocalQueue(name, "", threshold, EQueueDisp.PERMANENT);
        ++defined;
      }
    }
    
    mLogger.trace("ServerRepository::createPredefinedQueues() - Total pre-defined queues: {}, defined: {}", total, defined);
    
    mLogger.trace("ServerRepository::createPredefinedQueues() - OUT");
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
    sb.append(name()).append("(\n");
    ///
    /// TODO: COMPLETE
    ///
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
