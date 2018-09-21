package com.kas.mq.server;

import com.kas.infra.base.IObject;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.MqConnection;
import com.kas.mq.server.internal.SessionHandler;

/**
 * {@link IController} is an interface that will mediate between the {@link IMqServer}
 * and the {@link SessionHandler}. 
 * 
 * +----------------+      +----------------+      +----------------+
 * |                | =====|                |===>> |                |
 * |   IMqServer    |      |  IController   |      | SessionHandler |
 * |                | <<===|                |===== |                |
 * +----------------+      +----------------+      +----------------+
 *
 * For example:
 *   If an administrative client has issued a shutdown command for the server,
 * the {@link IController} is the one that initiates the termination process.
 *   On the other hand, when a new client connection is accepted,
 * the {@link IController} is responsible for creating new handler and submit it for execution.
 * 
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
   * Allocate new connection
   * 
   * @return return new {@link MqConnection}
   */
  public abstract MqConnection allocateConnection();
  
  /**
   * Release a connection
   * 
   * @param conn The {@link MqConnection} to release
   */
  public abstract void releaseConnection(MqConnection conn);
  
  /**
   * Shutdown all handlers and mark the main server's thread it should terminate
   */
  public abstract void shutdown();
  
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
