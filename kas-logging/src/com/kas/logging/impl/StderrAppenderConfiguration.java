package com.kas.logging.impl;

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
   */
  public StderrAppenderConfiguration replicate()
  {
    return new StderrAppenderConfiguration(mLoggingConfig);
  }
}
