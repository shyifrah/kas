package com.kas.mq.server;

import com.kas.mq.MqConfiguration;
import com.kas.mq.server.internal.QueueRepository;

public interface IMqServer
{
  /**
   * Get the {@link QueueRepository} object
   * 
   * @return the {@link QueueRepository} object
   */
  public abstract IRepository getRepository();
  
  /**
   * Get the {@link MqConfiguration} object
   * 
   * @return the {@link MqConfiguration} object
   */
  public abstract MqConfiguration getConfig();
  
  /**
   * Mark server it should stop
   */
  public abstract void stop();
  
  /**
   * Get indication if the server should stop
   * 
   * @return an indication if the server should stop
   */
  public abstract boolean isStopping();
}
