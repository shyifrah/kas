package com.kas.logging.appender.cons;

import com.kas.logging.appender.IAppenderConfiguration;
import com.kas.logging.impl.IAppender;
import com.kas.logging.impl.LoggingConfiguration;

/**
 * A STDERR appender configuration
 * 
 * @author Pippo
 */
public class StderrAppenderConfiguration extends AConsoleAppenderConfiguration
{
  /**
   * Construct the appender's configuration, providing the {@link LoggingConfiguration}
   * 
   * @param name The name of the appender
   * @param loggingConfig The {@link LoggingConfiguration}
   */
  public StderrAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    super(name, loggingConfig);
  }
  
  /**
   * Create an appender that uses this {@link IAppenderConfiguration} object
   * 
   * @return a new {@link IAppender} that is associated with this {@link IAppenderConfiguration}
   */
  public IAppender createAppender()
  {
    return new StderrAppender(this);
  }
}
