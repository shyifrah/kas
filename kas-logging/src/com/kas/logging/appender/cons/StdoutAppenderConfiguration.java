package com.kas.logging.appender.cons;

import com.kas.logging.impl.AppenderManager;
import com.kas.logging.impl.LoggingConfiguration;

/**
 * A STDOUT appender configuration
 * 
 * @author Pippo
 */
public class StdoutAppenderConfiguration extends ConsoleAppenderConfiguration
{
  /**
   * Construct the appender's configuration, providing the {@link LoggingConfiguration}
   */
  public StdoutAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(AppenderManager.cStdoutAppenderName, loggingConfig);
  }
  
  /**
   * Returns a replica of this {@link StdoutAppenderConfiguration}.
   * 
   * @return a replica of this {@link StdoutAppenderConfiguration}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public StdoutAppenderConfiguration replicate()
  {
    return new StdoutAppenderConfiguration(mLoggingConfig);
  }
}
