package com.kas.mq.server;

import com.kas.mq.IKasMqAppl;
import com.kas.mq.MqConfiguration;
import com.kas.mq.server.internal.QueueRepository;

public interface IMqServer extends IKasMqAppl
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
}
