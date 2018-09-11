package com.kas.mq.server.internal;

import java.util.Collection;
import com.kas.infra.base.AKasObject;
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
   * The queue regulator
   */
  private QueueRegulator mQueueRegulator;
  
  /**
   * Construct the server repository object.
   * 
   * @param config The {@link MqConfiguration}
   */
  public ServerRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mQueueRegulator = new QueueRegulator(mConfig);
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
    boolean success = true;
    
    mLogger.trace("ServerRepository::init() - Initializing repository...");
    success = mQueueRegulator.restore();
//    
//    if (success)
//    {
//      success = synch();
//    }
//    
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
    
    success = mQueueRegulator.backup();
    
    mLogger.debug("ServerRepository::term() - OUT, Returns=" + success);
    return success;
  }
  
//  private boolean synch()
//  {
//    MqClientImpl client = new MqClientImpl();
//    for (Map.Entry<String, NetworkAddress> entry : mConfig.getRemoteManagers().entrySet())
//    {
//      String mgrName = entry.getKey();
//      NetworkAddress mgrAddr = entry.getValue();
//      
//      client.connect(mgrAddr.getHost(), mgrAddr.getPort());
//      client.disconnect();
//    }
//    return true;
//  }
//
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
    MqLocalQueue queue = mQueueRegulator.defineQueue(name, threshold);
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
    MqLocalQueue queue = mQueueRegulator.deleteQueue(name);
    mLogger.debug("ServerRepository::deleteLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
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
    MqLocalQueue queue = mQueueRegulator.getQueue(name);
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
    return mQueueRegulator.getDeadQueue();
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
    return mQueueRegulator.getAll();
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
