package com.kas.mq.server;

import java.util.Collection;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.impl.internal.MqManager;

/**
 * A Repository of queues
 * 
 * @author Pippo
 */
public interface IRepository extends IInitializable, IObject
{
  /**
   * Define a {@link MqLocalQueue} object with the specified {@code name}
   * 
   * @param name The name of the queue
   * @param threshold The queue threshold
   * @return the {@link MqLocalQueue} object defined
   */
  public abstract MqLocalQueue defineLocalQueue(String name, int threshold);
  
  /**
   * Delete a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be deleted
   * @return the {@link MqLocalQueue} object deleted
   */
  public abstract MqLocalQueue deleteLocalQueue(String name);
  
  /**
   * Get information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   */
  public abstract Properties queryQueue(String name, boolean prefix, boolean all);
  
  /**
   * Get a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name The name of the local queue to be retrieved
   * @return the {@link MqLocalQueue} object or {@code null} if {@code name} is {@code null}, or there's no queue with this name.
   */
  public abstract MqLocalQueue getLocalQueue(String name);
  
  /**
   * Is a queue exists
   * 
   * @param name The name of the queue to be tested
   * @return {@code true} if a local queue named {@code name} exists, {@code false} otherwise 
   */
  public abstract boolean isLocalQueueExist(String name);
  
  /**
   * Get {@link MqManager} by its name 
   * 
   * @param name The name of the {@link MqManager}
   * @return the {@link MqManager}
   */
  public abstract MqManager getRemoteManager(String name);
  
  /**
   * Get the {@link MqLocalQueue} object representing the dead queue
   * 
   * @return the {@link MqLocalQueue} object of the dead queue
   */
  public abstract MqLocalQueue getDeadQueue();
  
  /**
   * Get a collection of all local queues
   * 
   * @return collection of all local queues
   */
  public abstract Collection<MqQueue> getLocalQueues();
}
