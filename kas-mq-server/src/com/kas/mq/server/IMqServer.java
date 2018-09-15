package com.kas.mq.server;

import com.kas.infra.base.IStoppable;
import com.kas.mq.IKasMqAppl;
import com.kas.mq.MqConfiguration;
import com.kas.mq.server.repo.ServerRepository;

public interface IMqServer extends IKasMqAppl, IStoppable
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
}
