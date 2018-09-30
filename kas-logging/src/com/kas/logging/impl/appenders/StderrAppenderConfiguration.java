package com.kas.logging.impl.appenders;

import com.kas.logging.impl.AppenderManager;
import com.kas.logging.impl.LoggingConfiguration;

/**
 * A STDERR appender configuration
 * 
 * @author Pippo
 */
public class StderrAppenderConfiguration extends ConsoleAppenderConfiguration
{
  /**
   * Construct the appender's configuration, providing the {@link LoggingConfiguration}
   */
  public StderrAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(AppenderManager.cStderrAppenderName, loggingConfig);
  }
  
  /**
   * Returns a replica of this {@link StderrAppenderConfiguration}.
   * 
   * @return a replica of this {@link StderrAppenderConfiguration}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public StderrAppenderConfiguration replicate()
  {
    return new StderrAppenderConfiguration(mLoggingConfig);
  }
}
