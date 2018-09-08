package com.kas.mq.server;

import java.util.Collection;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.mq.impl.internal.MqQueue;

/**
 * A Repository of queues
 * 
 * @author Pippo
 */
public interface IRepository extends IInitializable, IObject
{
  /**
   * Create a {@link MqQueue} object with the specified {@code name}
   * 
   * @param name The name of the queue
   * @param threshold The queue threshold
   * @return the {@link MqQueue} object created
   */
  public abstract MqQueue createQueue(String name, int threshold);
  
  /**
   * Remove a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be removed
   * @return the {@link MqQueue} object removed
   */
  public abstract MqQueue removeQueue(String name);
  
  /**
   * Get a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be retrieved
   * @return the {@link MqQueue} object or {@code null}, if {@code name} is {@code null}, or there's no queue with this name.
   */
  public abstract MqQueue getQueue(String name);
  
  /**
   * Is a queue exists
   * 
   * @param name The name of the queue to be tested
   * @return {@code true} if a queue named {@code name} exists, {@code false} otherwise 
   */
  public abstract boolean isQueueExist(String name);
  
  /**
   * Get the {@link MqQueue} object representing the dead queue
   * 
   * @return the {@link MqQueue} object of the dead queue
   */
  public abstract MqQueue getDeadQueue();
  
  /**
   * Get a collection of all queues
   * 
   * @return collection of all queues
   */
  public abstract Collection<MqQueue> getElements();
}
