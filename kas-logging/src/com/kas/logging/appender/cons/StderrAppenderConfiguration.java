package com.kas.logging.appender.cons;

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
   * 
   * @param name The name of the appender
   * @param loggingConfig The {@link LoggingConfiguration}
   */
  public StderrAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    super(name, loggingConfig);
  }
}
