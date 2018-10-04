package com.kas.logging.impl;

import com.kas.logging.appender.IAppenderConfiguration;
import com.kas.logging.appender.cons.StderrAppender;

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
  StderrAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    super(name, loggingConfig);
    refresh();
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
