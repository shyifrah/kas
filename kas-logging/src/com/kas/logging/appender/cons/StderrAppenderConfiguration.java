package com.kas.logging.appender.cons;

import com.kas.logging.impl.LogSystem;
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
    super(LogSystem.cStderrAppenderName, loggingConfig);
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
