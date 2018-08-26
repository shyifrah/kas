package com.kas.mq.server;

import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.server.internal.QueueRepository;
import com.kas.mq.server.internal.SessionHandler;

/**
 * The handler is responsible for updating the read/write data members of the {@link SessionHandler}
 * 
 * @author Pippo
 */
public interface IHandler extends IObject
{
  /**
   * Get the session ID
   * 
   * @return the session ID
   */
  public abstract UniqueId getSessionId();
  
  /**
   * Get the active user name
   * 
   * @return the active user name
   */
  public abstract String getActiveUserName();
  
  /**
   * Set the active user name
   * 
   * @param username The active user name
   */
  public abstract void setActiveUserName(String username);
  
  /**
   * Get the active queue
   * 
   * @return the active queue, or {@code null} if closed
   */
  public abstract MqQueue getActiveQueue();
  
  /**
   * Set the active queue
   * 
   * @param queue The active queue
   */
  public abstract void setActiveQueue(MqQueue queue);
  
  /**
   * Get the controller
   * 
   * @return the {@link SessionController} object
   */
  public abstract IController getController();
  
  /**
   * Get repository
   * 
   * @return the {@link QueueRepository} object
   */
  public abstract IRepository getRepository();
  
  /**
   * Get configuration
   * 
   * @return the {@link MqConfiguration} object
   */
  public abstract MqConfiguration getConfig();
  
  /**
   * Get network address
   * 
   * @return the {@link IMessenger}'s {@link NetworkAddress} object
   */
  public abstract NetworkAddress getNetworkAddress();
}
