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
   * Get indication if a user's password matches the one defined in the {@link MqConfiguration}
   * 
   * @param user The user's name
   * @param pass The user's password
   * @return {@code true} if the user's password matches the one defined in the {@link MqConfiguration}, {@code false} otherwise
   */
  public abstract boolean isPasswordMatch(String user, String pass);
  
  /**
   * Get repository
   * 
   * @return the {@link QueueRepository} object
   */
  public abstract IRepository getRepository();
  
  /**
   * Get network address
   * 
   * @return the {@link IMessenger}'s {@link NetworkAddress} object
   */
  public abstract NetworkAddress getNetworkAddress();
}
