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
   * Get handlers map
   * 
   * @return the handlers map
   */
  public abstract Map<UniqueId, SessionHandler> getHandlers();
  
  /**
   * Shutdown all handlers and mark the main server's thread it should terminate
   */
  public abstract void shutdown();
  
  /**
   * Terminate handler that servers session {@code sessId}.
   * 
   * @param sessId The ID assigned to the session to be terminated
   * @return {@code true} if session was found and was terminated, {@code false} otherwise
   */
  public abstract boolean termHandler(UniqueId sessId);
  
  /**
   * A callback that is invoked under the handler's thread right before
   * the handler starts its run() method.
   * 
   * @param handler The handler that invoked the callback
   */
  public abstract void onHandlerStart(SessionHandler handler);
  
  /**
   * A callback that is invoked under the handler's thread right before
   * the handler ends its run() method.
   * 
   * @param handler The handler that invoked the callback
   */
  public abstract void onHandlerEnd(SessionHandler handler);
}
