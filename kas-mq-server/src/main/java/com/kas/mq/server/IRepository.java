package com.kas.mq.server;

import java.util.Collection;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.config.IBaseListener;
import com.kas.infra.typedef.StringList;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.internal.MqManager;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.internal.MqRemoteQueue;
import com.kas.mq.server.repo.MqLocalManager;
import com.kas.mq.server.repo.MqRemoteManager;

/**
 * A Repository of queues
 * 
 * @author Pippo
 */
public interface IRepository extends IBaseListener, IInitializable, IObject
{
  /**
   * Alter a {@link MqLocalQueue} object with the specified {@code name}
   * 
   * @param name
   *   The name of the queue
   * @param qProps
   *   The queue properties to be altered
   * @return
   *   the {@link MqLocalQueue} object defined
   */  
  public abstract MqLocalQueue alterLocalQueue(String name, Properties qProps);
  
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
  public abstract MqLocalQueue defineLocalQueue(String name, String desc, int threshold, EQueueDisp disp);
  
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
  public abstract MqRemoteQueue defineRemoteQueue(String qmgr, String queue);
  
  /**
   * Delete a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name
   *   The name of the queue to be deleted
   * @return
   *   the {@link MqLocalQueue} object deleted
   */
  public abstract MqLocalQueue deleteLocalQueue(String name);
  
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
  public abstract MqRemoteQueue deleteRemoteQueue(String qmgr, String queue);
  
  /**
   * Get a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name
   *   The name of the local queue to be retrieved
   * @return
   *   the {@link MqLocalQueue} object or {@code null} if {@code name} is {@code null},
   *   or there's no queue with this name.
   */
  public abstract MqLocalQueue getLocalQueue(String name);
  
  /**
   * Get a {@link MqRemoteQueue} object with the specified {@code name}.
   * 
   * @param name
   *   The name of the remote queue to be retrieved
   * @return
   *   the {@link MqRemoteQueue} object or {@code null} if {@code name} is {@code null},
   *   or there's no queue with this name.
   */
  public abstract MqRemoteQueue getRemoteQueue(String name);
  
  /**
   * Get a {@link MqQueue} object with the specified {@code name}.<br>
   * We search for a local queue with the specified name, and if we don't find,
   * we look for a remote one.
   * 
   * @param name
   *   The name of the queue to be retrieved
   * @return
   *   the {@link MqRemoteQueue} object or {@code null} if {@code name} is {@code null},
   *   or there's no queue with this name.
   */
  public abstract MqQueue getQueue(String name);
  
  /**
   * Get information regarding local queues whose name begins with the specified prefix.
   * 
   * @param name
   *   The queue name/prefix.
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix.
   *   If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a {@link StringList} object that holds the queried data
   */
  public abstract StringList queryLocalQueues(String name, boolean prefix, boolean all);
  
  /**
   * Get information regarding remote queues whose name begins with the specified prefix.
   * 
   * @param name
   *   The queue name/prefix.
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix.
   *   If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a {@link StringList} object that holds the queried data
   */
  public abstract StringList queryRemoteQueues(String name, boolean prefix, boolean all);
  
  /**
   * Get information regarding all queues whose name begins with the specified prefix.<br>
   * This method will return the contents of both {@link #queryLocalQueues(String, boolean, boolean)}
   * and {@link #queryRemoteQueues(String, boolean, boolean)} altogether.
   * 
   * @param name
   *   The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix
   *   If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all
   *   If {@code true}, display all information on all queues, otherwise, display only names 
   * @return
   *   a {@link StringList} object that holds the queried data
   */
  public abstract StringList queryQueues(String name, boolean prefix, boolean all);
  
  /**
   * Get {@link MqManager} by its name 
   * 
   * @param name
   *   The name of the {@link MqManager}
   * @return
   *   the {@link MqManager}
   */
  public abstract MqManager getRemoteManager(String name);
  
  /**
   * Get all remote {@link MqRemoteManager managers} 
   * 
   * @return
   *   the {@link MqManager}
   */
  public abstract Collection<MqRemoteManager> getRemoteManagers();
  
  /**
   * Get the local {@link MqLocalManager} 
   * 
   * @return
   *   the local {@link MqLocalManager}
   */
  public abstract MqLocalManager getLocalManager();
  
  /**
   * Get the {@link MqLocalQueue} object representing the dead queue
   * 
   * @return
   *   the {@link MqLocalQueue} object of the dead queue
   */
  public abstract MqLocalQueue getDeadQueue();
  
  /**
   * Get a collection of all local queues
   * 
   * @return
   *   collection of all local queues
   */
  public abstract Collection<MqQueue> getLocalQueues();
}
