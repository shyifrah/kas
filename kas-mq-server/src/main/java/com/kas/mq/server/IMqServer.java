package com.kas.mq.server;

import com.kas.appl.IKasApp;
import com.kas.db.DbConnectionPool;
import com.kas.mq.server.repo.ServerRepository;

public interface IMqServer extends IKasApp
{
  /**
   * Get the {@link ServerRepository} object
   * 
   * @return the {@link ServerRepository} object
   */
  public abstract IRepository getRepository();
  
  /**
   * Get the {@link MqConfiguration} object
   * 
   * @return the {@link MqConfiguration} object
   */
  public abstract MqConfiguration getConfig();
  
  /**
   * Get the {@link DbConnectionPool} object
   * 
   * @return the {@link DbConnectionPool} object
   */
  public abstract DbConnectionPool getDbConnectionPool();
  
  /**
   * Indicate that the caller wants the object to change its state to "stopping" 
   */
  public abstract void stop();
}
