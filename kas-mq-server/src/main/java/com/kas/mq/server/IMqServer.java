package com.kas.mq.server;

import com.kas.appl.IKasApp;
import com.kas.db.DbConnectionPool;
import com.kas.infra.base.IStoppable;
import com.kas.mq.server.repo.ServerRepository;

public interface IMqServer extends IKasApp, IStoppable
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
}
