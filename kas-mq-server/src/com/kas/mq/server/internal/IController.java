package com.kas.mq.server.internal;

import com.kas.infra.base.IObject;
import com.kas.mq.MqConfiguration;

/**
 * {@link IController} is an interface that will provide functionality for {@link ClientHandler ClientHandlers}.
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
   * Get indication if a user's password matches the one defined in the {@link MqConfiguration}
   * 
   * @param user The user's name
   * @param pass The user's password
   * @return {@code true} if the user's password matches the one defined in the {@link MqConfiguration}, {@code false} otherwise
   */
  public abstract boolean isPasswordMatch(String user, String pass);
}
