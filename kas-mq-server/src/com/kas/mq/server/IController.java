package com.kas.mq.server;

import java.util.Map;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.mq.MqConfiguration;
import com.kas.mq.server.internal.SessionHandler;

/**
 * {@link IController} is an interface that will provide functionality for {@link SessionHandler ClientHandlers}.
 * @author Pippo
 */
public interface IController extends IObject
{
  /**
   * Get the controller's MQ configuration
   * 
   * @return the controller's MQ configuration
   */
  public abstract MqConfiguration getConfig();
  
  /**
   * Get the server's repository
   * 
   * @return the server's repository
   */
  public abstract IRepository getRepository();
  
  /**
   * Get handlers map
   * 
   * @return the handlers map
   */
  public abstract Map<UniqueId, SessionHandler> getHandlers();
  
  /**
   * Shutdown KAS/MQ server
   */
  public abstract void shutdown();
}
