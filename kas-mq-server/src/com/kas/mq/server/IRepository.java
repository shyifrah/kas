package com.kas.mq.server;

import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.mq.impl.MqQueue;

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
   * @return the {@link MqQueue} object created
   */
  public abstract MqQueue createQueue(String name);
  
  /**
   * Get or Create a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue
   * @return the {@link MqQueue} object created or retrieved
   */
  public abstract MqQueue getOrCreateQueue(String name);
  
  /**
   * Remove a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be removed
   * @return the {@link MqQueue} object removed
   */
  public abstract MqQueue removeQueue(String name);
  
  /**
   * Remove a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be removed
   * @return the {@link MqQueue} object removed
   */
  public abstract MqQueue getQueue(String name);
}
