package com.kas.logging.appender;

import com.kas.infra.base.IObject;
import com.kas.logging.impl.IAppender;

/**
 * Appender's configuration
 * 
 * @author Pippo
 */
public interface IAppenderConfiguration extends IObject
{
  /**
   * Get the appender's name
   * 
   * @return the name associated with this appender's configuration
   */
  public abstract String getName();
  
  /**
   * Create an appender that uses this {@link IAppenderConfiguration} object
   * 
   * @return a new {@link IAppender} that is associated with this {@link IAppenderConfiguration}
   */
  public abstract IAppender createAppender();
}
