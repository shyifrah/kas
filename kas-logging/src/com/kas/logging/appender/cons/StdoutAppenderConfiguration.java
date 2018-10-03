package com.kas.logging.appender.cons;

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
   * 
   * @param name The name of the appender
   * @param loggingConfig The {@link LoggingConfiguration}
   */
  public StdoutAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    super(name, loggingConfig);
  }
}
